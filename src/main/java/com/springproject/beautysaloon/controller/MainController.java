package com.springproject.beautysaloon.controller;

import com.springproject.beautysaloon.dto.AdminUserDto;
import com.springproject.beautysaloon.dto.ClientDto;
import com.springproject.beautysaloon.dto.MasterDto;
import com.springproject.beautysaloon.dto.RequestDto;
import com.springproject.beautysaloon.model.*;
import com.springproject.beautysaloon.repository.*;
import com.springproject.beautysaloon.service.FeedbackService;
import com.springproject.beautysaloon.service.ProcedureService;
import com.springproject.beautysaloon.service.RequestService;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping
public class MainController {

    private final SpecialityRepository specialityRepository;
    private final WorkDayRepository workDayRepository;
    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final FeedbackService feedbackService;
    private final ProcedureService procedureService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public MainController(ProcedureRepository procedureRepository, SpecialityRepository specialityRepository, WorkDayRepository workDayRepository, RequestRepository requestRepository, RequestService requestService, FeedbackService feedbackService, ProcedureService procedureService, UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.specialityRepository = specialityRepository;
        this.workDayRepository = workDayRepository;
        this.requestRepository = requestRepository;
        this.requestService = requestService;
        this.feedbackService = feedbackService;
        this.procedureService = procedureService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/auth/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/team")
    public String getTeam(){
        return "team";
    }

    @GetMapping("register")
    public String getRegisterForm(Model model) {
        ClientDto clientDto = new ClientDto();
        clientDto.setVisits(0);
        clientDto.setRole(Role.CLIENT);
        clientDto.setStatus(Status.ACTIVE);
        model.addAttribute("clientDto", clientDto);
        return "register";
    }

    @GetMapping("/client-home")
    @PreAuthorize(value = "hasAuthority('client:read')")
    public String getClientHomePage(){
        return "client/client-home";
    }

    @GetMapping("/master-home")
    @PreAuthorize(value = "hasAnyAuthority('master:read','developers:read')")
    public String getMasterHomePage(Model model){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return getMasterHomePageDate(model, formatter.format(new Date()));
    }

    @GetMapping("/master-home/{date}")
    public String getMasterHomePageDate(Model model, @PathVariable(value = "date")String date){
        Integer doneRequests = 0;
        date += " 00:00:00";
        org.springframework.security.core.userdetails.User masterDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> master = userService.findByUsername(masterDetails.getUsername());
        if(master.isPresent()){

            MasterDto masterDto = MasterDto.fromUser(master.get());
            Long masterId = masterDto.getId();
            Integer rating = userService.getMasterRatingById(masterId);
            List<Request> requestListAll = requestService.findAllByMasterId(masterId);
            List<Feedback> feedbackList = feedbackService.findAllByMasterId(masterId);
            List<Request> requestListByDate = requestService.findAllRequestsByMasterIdAndDate(masterId, Timestamp.valueOf(date));
            List<WorkDay> workDayList = workDayRepository.findAllByMasterId(masterId);

            WorkDay selectedWorkDay = workDayRepository.findWorkDayByDayAndMasterId(java.sql.Date.valueOf(date.substring(0,10)), masterId);
            Collections.swap(workDayList, 0, workDayList.indexOf(selectedWorkDay));

            List<User> userList = requestRepository.findAllClientsByMasterId(masterId);

            model.addAttribute("clientSize", userList.size());
            model.addAttribute("workDayList", workDayList);
            model.addAttribute("date", selectedWorkDay);
            model.addAttribute("feedbackList", feedbackList.size());
            model.addAttribute("requestListTotalSize", requestListAll.size());
            model.addAttribute("requestListByDate", requestListByDate);

            for(Request request : requestListByDate){
                if(request.isDone()){
                   doneRequests++;
                }
            }
            double percentage = 0;
            if(!requestListByDate.isEmpty()){
                percentage = (doneRequests * 100) / requestListByDate.size();
            }

            model.addAttribute("master",masterDto);
            model.addAttribute("rating", rating);
            model.addAttribute("masterName", master.get().getName());

            model.addAttribute("pass", percentage);
            model.addAttribute("fail", 100 - percentage);

        }

        return "master/master-home";
    }

    @GetMapping("/admin-home")
    @PreAuthorize(value = "hasAnyAuthority('developers:read')")
    public String getAdminHomePage(Model model){
        org.springframework.security.core.userdetails.User masterDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> master = userService.findByUsername(masterDetails.getUsername());
        if(master.isPresent()){
            AdminUserDto admin = AdminUserDto.fromUser(master.get());
            model.addAttribute("name",admin.getName());
        }
        return "admin/admin-home";
    }

    @GetMapping("/admin-home/{role}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String getAllEntities(Model model, @PathVariable(value = "role") String role){
        return findPaginatedUsers(1,"name", "asc", model, role);
    }

    @GetMapping("/admin-home/{role}/page/{pageNo}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String findPaginatedUsers(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDirection") String sortDirection,
                                Model model, @PathVariable(value = "role") String roleStr)
    {
        int pageSize = 10;

        Role role = Role.UNKNOWN;
        String pageRedirect = "admin/admin-home";

        switch (roleStr) {
            case "clients":
                role = Role.CLIENT;
                pageRedirect = "client/client-list";
                break;
            case "masters":
                role = Role.MASTER;
                pageRedirect = "master/master-list";
                break;
            case "admins":
                role = Role.ADMIN;
                pageRedirect = "admin/admin-list";
                break;
        }

        Page<User> page = userService.findPaginated(pageNo, pageSize, sortField, sortDirection, role);

        List<User> entityList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("entityList", entityList);

        return pageRedirect;
    }

    @GetMapping("/admin-home/requests")
    @PreAuthorize("hasAuthority('developers:read')")
    public String getAllRequests(Model model){
        return getAllRequestsPaginated(1,"date", "desc", model);
    }

    @GetMapping("/admin-home/requests/page/{pageNo}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String getAllRequestsPaginated(@PathVariable(value = "pageNo") int pageNo,
                                          @RequestParam("sortField") String sortField,
                                          @RequestParam("sortDirection") String sortDirection,
                                          Model model){

        int pageSize = 10;
        Page<Request> page = requestService.findPaginated(pageNo, pageSize, sortField, sortDirection);
        List<Request> requestList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("requestList",requestList);
        return "request/request-list";
    }

    @GetMapping("/admin-home/requests/{role}/{id}")
    @PreAuthorize(value = "hasAuthority('developers:read')")
    public String getAllClientRequests(Model model, @PathVariable(name = "role") String roleStr, @PathVariable(value = "id") Long id){
        return getClientRequestsPaginated(1, "date", "desc", roleStr, id, model);
    }

    @GetMapping("/admin-home/masters/new/master")
    @PreAuthorize("hasAuthority('developers:read')")
    public String getForm(Model model){
        List<Speciality> specialityList = specialityRepository.findAll();
        MasterDto master = new MasterDto();
        master.setRole(Role.MASTER);
        master.setStatus(Status.ACTIVE);
        model.addAttribute("master", master);
        model.addAttribute("specialityList", specialityList);
        return "user/add-user-form";
    }

    @PostMapping("/save-master")
    @PreAuthorize(value = "hasAuthority('developers:write')")
    public String saveMaster(@ModelAttribute("master") MasterDto masterDto){
        List<Speciality> specialityList = new ArrayList<>();
        for(String specialityStr : masterDto.getSpecialityList()){
            specialityList.add(specialityRepository.getOne(Long.valueOf(specialityStr)));
        }
        User master = masterDto.toUser();
        master.setSpecialityList(specialityList);
        master.setPassword(passwordEncoder.encode(masterDto.getPassword()));
        userService.saveUser(master);
        return "redirect:/admin-home/masters";
    }

    @GetMapping("/admin-home/delete/{role}/{id}")
    @PreAuthorize(value = "hasAuthority('developers:write')")
    public String deleteUser(@PathVariable(value = "role")String roleStr,
                             @PathVariable(value = "id")Long id){
        userService.deleteUser(id);
        return "redirect:/admin-home/" + roleStr;
    }


    @GetMapping("/admin-home/requests/{role}/{id}/page/{pageNo}")
    @PreAuthorize(value = "hasAuthority('developers:read')")
    public String getClientRequestsPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDirection") String sortDirection,
                                @PathVariable(name = "role") String roleStr,
                                @PathVariable(value = "id") Long id,
                                Model model)
    {
        int pageSize = 10;
        Optional<User> client = userRepository.findById(id);
        Page<Request> page = null;

        if(roleStr.equals("client") && client.isPresent()){
            page = requestService.findPaginatedClient(pageNo,pageSize,sortField,sortDirection, client.get());
        }else if(roleStr.equals("master")){
            page = requestService.findPaginatedMaster(pageNo, pageSize, sortField, sortDirection, id);
        }


        List<Request> requestList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("requestList",requestList);

        return "request/request-list";
    }

    @GetMapping("/admin-home/requests/new/request")
    @PreAuthorize(value = "hasAnyAuthority('developers:write', 'masters:write')")
    public String createRequest(Model model) {
        java.util.Date date = new java.util.Date();
        Timestamp today = new Timestamp(date.getTime());
        return "redirect:/admin-home/requests/new/request/client/2/procedure/1/master/3/date/" + today.toString().substring(0,10) + " 00:00:00";
    }

    @GetMapping("admin-home/requests/new/request/client/{clientId}/procedure/{procedureId}/master/{masterId}/date/{date}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String setRequestParams(Model model, @PathVariable(value = "procedureId") Long procedureId,
                                   @PathVariable(value = "masterId")Long masterId,
                                   @PathVariable(value = "date") String timestamp,
                                   @PathVariable(value = "clientId")Long clientId){
        List<String> formattedList = new ArrayList<>();

        List<Procedure> procedureList = procedureService.findAllWithIdentityName();
        Optional<Procedure> selectedProcedure = procedureService.findById(procedureId);

        if(selectedProcedure.isPresent() && !procedureList.get(0).getId().equals(procedureId)) {
            selectedProcedure.ifPresent(procedure -> Collections.swap(procedureList,0 ,procedureList.indexOf(procedure)));
        }
        List<User> masterList = userRepository.findAllMastersByProcedureName(selectedProcedure.get().getName());
        Optional<User> selectedMaster = userRepository.findById(masterId);

        if(selectedMaster.isPresent()){
            if(masterList.contains(selectedMaster.get())){
                selectedMaster.ifPresent(master -> Collections.swap(masterList, 0, masterList.indexOf(master)));
            }
            else {
                return "redirect:/admin-home/requests/new/request/client/2/procedure/" + selectedProcedure.get().getId() + "/master/" + masterList.get(0).getId() + "/date/" + timestamp;
            }
        }

        List<WorkDay> workDayList = workDayRepository.findAllByMasterId(masterId);
        Calendar calendar = Calendar.getInstance();
        for(WorkDay date : workDayList){
            calendar.setTime(date.getDay());
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d yyyy", Locale.ENGLISH);
            formattedList.add(formatter.format(calendar.getTime()));
        }

        List<Request> requestList = requestService.findAllRequestsByMasterIdAndDate(masterId, Timestamp.valueOf(timestamp));
        Time fromTime = workDayRepository.findStartTimeByMasterId(masterId, java.sql.Date.valueOf(timestamp.substring(0,10)));
        Time tillTime = workDayRepository.findEndTimeByMasterId(masterId, java.sql.Date.valueOf(timestamp.substring(0,10)));

        int iterations = 0;
        if(fromTime != null && tillTime != null ){
            LocalTime from = fromTime.toLocalTime();
            LocalTime till = tillTime.toLocalTime();
            LocalTime result = till.minusHours(from.getHour());
            iterations = result.getHour() * 60 / 15;
        }


        List<Time> slots = new ArrayList<>();

        for(int i = 0; i < iterations; i++){
            if(i < 12 || i > 15){
                slots.add(Time.valueOf(fromTime.toString()));
            }
            fromTime.setTime(fromTime.getTime() + TimeUnit.MINUTES.toMillis(15));
        }

        Time duration = null;
        for(Request request : requestList){
            for(int i =0; i < slots.size(); i++){

                if(request.getTime().equals(slots.get(i))){
                    duration = new Time(request.getProcedure().getDuration().getTime());
                    duration.setTime(duration.getTime() + slots.get(i).getTime() + TimeUnit.HOURS.toMillis(3));
                }

                if((duration != null) && slots.get(i).getTime() <= duration.getTime()){
                    slots.remove(i);
                    i--;
                }
            }
            duration = null;
        }

        List<User> clientList = userService.findAllClients();
        Optional<User> selectedClient = userService.findById(clientId);
        if(selectedClient.isPresent() && !clientList.get(0).getId().equals(selectedClient.get().getId())){
            selectedClient.ifPresent(client -> Collections.swap(clientList, 0, clientList.indexOf(client)));
        }

        model.addAttribute("clientList", clientList);
        model.addAttribute("day", timestamp);
        model.addAttribute("timeslots", slots);
        model.addAttribute("workDayList", formattedList);
        model.addAttribute("masterList", masterList);
        model.addAttribute("procedureList", procedureList);
        model.addAttribute("request", new RequestDto());


        return "request/add-request-form";
    }

    @PostMapping("/save-request")
    @PreAuthorize("hasAuthority('developers:write')")
    public String saveRequest(@ModelAttribute(value = "request") RequestDto requestDto){
        Request request = requestDto.toRequest();
        requestService.saveRequest(request);
        return "redirect:/admin-home/requests";
    }
    
}
