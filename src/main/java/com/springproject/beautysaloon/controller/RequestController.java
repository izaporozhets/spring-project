package com.springproject.beautysaloon.controller;

import com.springproject.beautysaloon.dto.DoneResponse;
import com.springproject.beautysaloon.dto.MasterDto;
import com.springproject.beautysaloon.dto.RequestDto;
import com.springproject.beautysaloon.dto.UserDto;
import com.springproject.beautysaloon.model.Procedure;
import com.springproject.beautysaloon.model.Request;
import com.springproject.beautysaloon.model.Role;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.repository.*;
import com.springproject.beautysaloon.service.ProcedureService;
import com.springproject.beautysaloon.service.RequestService;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class RequestController {

    private final RequestService requestService;
    private final ProcedureService procedureService;
    private final UserRepository userRepository;
    private final UserService userService;

    public RequestController(RequestService requestService, ProcedureService procedureService, UserRepository userRepository, UserService userService) {
        this.requestService = requestService;
        this.procedureService = procedureService;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @GetMapping("/admin-home/request/info/{id}")
    @PreAuthorize("hasAnyAuthority('developers:read','developers:write')")
    public String getRequestInfo(Model model, @PathVariable(name = "id") Long id) {
        Optional<Request> request = requestService.findById(id);
        Optional<User> client = userService.findById(request.get().getClient().getId());
        Optional<User> master = userService.findById(request.get().getMaster().getId());
        UserDto userDto = UserDto.fromUser(client.get());
        MasterDto masterDto = MasterDto.fromUser(master.get());
        List<Time> slots = requestService.getTimeSlots(masterDto.getId(), request.get().getDate().toString().substring(0,10), request.get().getProcedure().getDuration());
        List<String> formattedList = requestService.getWorkingDays(masterDto.getId());

        request.ifPresent(value -> model.addAttribute("request", value));
        model.addAttribute("workDayList",formattedList);
        model.addAttribute("day", request.get().getDate().toString());
        model.addAttribute("timeslots", slots);
        model.addAttribute("master", masterDto);
        model.addAttribute("client", userDto);
        return "request/request-info";
    }

    @GetMapping("admin-home/requests/new/request/client/{clientId}/procedure/{procedureId}/master/{masterId}/date/{date}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String setRequestParams(Model model, @PathVariable(value = "procedureId") Long procedureId,
                                   @PathVariable(value = "masterId") Long masterId,
                                   @PathVariable(value = "date") String timestamp,
                                   @PathVariable(value = "clientId") Long clientId) {

        List<Procedure> procedureList = procedureService.findAllWithIdentityName();
        Optional<Procedure> selectedProcedure = procedureService.findById(procedureId);

        if (selectedProcedure.isPresent() && !procedureList.get(0).getId().equals(procedureId)) {
            selectedProcedure.ifPresent(procedure -> Collections.swap(procedureList, 0, procedureList.indexOf(procedure)));
        }


        List<User> masterList = userRepository.findAllMastersBySpecialityId(selectedProcedure.get().getSpeciality().getId());
        Optional<User> selectedMaster = userRepository.findById(masterId);

        if (selectedMaster.isPresent()) {
            if (masterList.contains(selectedMaster.get())) {
                selectedMaster.ifPresent(master -> Collections.swap(masterList, 0, masterList.indexOf(master)));
            } else {
                return "redirect:/admin-home/requests/new/request/client/2/procedure/" + selectedProcedure.get().getId() + "/master/" + masterList.get(0).getId() + "/date/" + timestamp;
            }
        }


        List<String> formattedList = requestService.getWorkingDays(masterId);
        List<Time> slots = requestService.getTimeSlots(masterId, timestamp, selectedProcedure.get().getDuration());

        List<User> clientList = userService.findAllClients();
        Optional<User> selectedClient = userService.findById(clientId);
        if (selectedClient.isPresent() && !clientList.get(0).getId().equals(selectedClient.get().getId())) {
            selectedClient.ifPresent(client -> Collections.swap(clientList, 0, clientList.indexOf(client)));
        }

        model.addAttribute("clientList", clientList);
        model.addAttribute("day", timestamp + " 00:00:00");
        model.addAttribute("timeslots", slots);
        model.addAttribute("workDayList", formattedList);
        model.addAttribute("masterList", masterList);
        model.addAttribute("procedureList", procedureList);
        model.addAttribute("request", new RequestDto());


        return "request/add-request-form";
    }

    @GetMapping("/admin-home/requests")
    @PreAuthorize("hasAuthority('developers:read')")
    public String getAllRequests(Model model) {
        return getAllRequestsPaginated(1, "date", "desc", model);
    }

    @GetMapping("/admin-home/requests/page/{pageNo}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String getAllRequestsPaginated(@PathVariable(value = "pageNo") int pageNo,
                                          @RequestParam("sortField") String sortField,
                                          @RequestParam("sortDirection") String sortDirection,
                                          Model model) {

        int pageSize = 10;
        Page<Request> page = requestService.findPaginated(pageNo, pageSize, sortField, sortDirection);

        List<Request> requestList = page.getContent();
        model.addAttribute("doneResponse", new DoneResponse());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("requestList", requestList);
        return "request/request-list";
    }

    @PostMapping("/save-request")
    @PreAuthorize("hasAnyAuthority('developers:write', 'client:write')")
    public String saveRequest(@ModelAttribute(value = "request") RequestDto requestDto){

        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        Request request = requestDto.toRequest();
        requestService.saveRequest(request);

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().contains("client")) {
                return "redirect:/client-home/" + requestDto.getClient().getId() + "/requests";
            }
            if (authority.getAuthority().contains("developers")) {
                return "redirect:/admin-home/requests";
            }
        }
        return "error";
    }

    @RequestMapping("/done-request")
    @PreAuthorize(value = "hasAuthority('developers:read')")
    public String doneRequest(@RequestParam(name = "reqId")Long reqId, @RequestParam(name = "clientId")Long clientId) {
        Optional<Request> request = requestService.findById(reqId);
        if(request.isPresent() && !request.get().isDone()){
            userRepository.incrementVisitsById(clientId);
        }else {
            userRepository.decrementVisitsById(clientId);
        }
        requestService.setDone(reqId);
        return "redirect:/admin-home/requests";
    }

    @PostMapping("/change-time")
    @PreAuthorize(value = "hasAuthority('developers:write')")
    public String changeTime(@RequestParam(name = "time")String time,
                             @RequestParam(name = "date")String day,
                             @RequestParam(name = "reqId")Long reqId)
    {
        Optional<Request> request = requestService.findById(reqId);
        request.get().setDate(Timestamp.valueOf(day + " 00:00:00"));
        request.get().setTime(Time.valueOf(time));
        requestService.saveRequest(request.get());
        return "redirect:/admin-home/requests";
    }

    @GetMapping("/admin-home/requests/new/request")
    @PreAuthorize(value = "hasAnyAuthority('developers:write', 'masters:write')")
    public String createRequest(Model model) {
        java.util.Date date = new java.util.Date();
        Timestamp today = new Timestamp(date.getTime());
        return "redirect:/admin-home/requests/new/request/client/2/procedure/1/master/3/date/" + today.toString().substring(0,10);
    }

    @GetMapping("/admin-home/requests/{role}/{id}")
    @PreAuthorize(value = "hasAuthority('developers:read')")
    public String getAllUserRequests(Model model, @PathVariable(name = "role") String roleStr, @PathVariable(value = "id") Long id){
        return getRequestsByRole(1, "date", "desc", roleStr, id, model);
    }


    @GetMapping("/admin-home/requests/{role}/{id}/page/{pageNo}")
    @PreAuthorize(value = "hasAuthority('developers:read')")
    public String getRequestsByRole(@PathVariable(value = "pageNo") int pageNo,
                                    @RequestParam("sortField") String sortField,
                                    @RequestParam("sortDirection") String sortDirection,
                                    @PathVariable(name = "role") String roleStr,
                                    @PathVariable(value = "id") Long id,
                                    Model model)
    {
        int pageSize = 10;
        Optional<User> user = userRepository.findById(id);
        Page<Request> page = null;
        if(user.isPresent()) {
            page = requestService.findPaginatedByPersonId(pageNo, pageSize, sortField, sortDirection, id);
        }
        List<Request> requestList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("requestList",requestList);
        model.addAttribute("userId", id);
        model.addAttribute("role", roleStr);
        return "request/request-list-by-role";
    }

    @PostMapping("/delete-request")
    @PreAuthorize(value = "hasAuthority('developers:write')")
    public String deleteRequest(@RequestParam("reqId")Long reqId){
        requestService.deleteById(reqId);
        return "redirect:/admin-home/requests";
    }

    @GetMapping("/client-home/{id}/requests")
    @PreAuthorize(value = "hasAuthority('client:read')")
    public String getClientRequests(Model model, @PathVariable(name = "id")Long id){
        return getClientRequestsPaginated(1,"date", "desc", id, model);
    }

    @GetMapping("/client-home/{id}/requests/page/{pageNo}")
    @PreAuthorize(value = "hasAuthority('client:read')")
    public String getClientRequestsPaginated(@PathVariable(value = "pageNo") int pageNo,
                                             @RequestParam("sortField") String sortField,
                                             @RequestParam("sortDirection") String sortDirection,
                                             @PathVariable(value = "id") Long id, Model model){
        int pageSize = 10;
        Page<Request> page = requestService.findPaginatedByPersonId(pageNo, pageSize, sortField, sortDirection, id);
        List<Request> requestList = page.getContent();

        int requestRows = 1;
        int reqListSize = requestList.size();
        if(reqListSize % 4 != 0){
            requestRows = requestRows / 4;
            requestRows++;
        }
        else {
            requestRows = reqListSize;
        }
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("requestList",requestList);
        model.addAttribute("clientId", id);
        model.addAttribute("rows", requestRows);

        return "client/client-request-list";
    }

    @GetMapping("/client-home/{id}/book")
    @PreAuthorize(value = "hasAuthority('client:read')")
    public String bookRequest(Model model, @PathVariable("id")Long clientId){
        if(getAuthenticatedUserId().equals(clientId)){
            Long masterId = userService.getFirstMaster(Role.MASTER).getId();
            Long procedureId = procedureService.findFirstByCost().getId();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String date = formatter.format(new Date());
            return bookRequestParams(model, clientId, procedureId, masterId, date);
        }
        return "error";
    }

    @GetMapping("/client-home/{id}/book/procedure/{procedureId}/master/{masterId}/date/{date}")
    public String bookRequestParams(Model model, @PathVariable("id")Long id, @PathVariable("procedureId") Long procId,
                              @PathVariable("masterId")Long masterId, @PathVariable("date")String date){
        if(getAuthenticatedUserId().equals(id)){
            Optional<User> client = userService.findById(id);

            List<Procedure> procedureList = procedureService.findAllWithIdentityName();
            Optional<Procedure> selectedProcedure = procedureService.findById(procId);

            if (selectedProcedure.isPresent() && !procedureList.get(0).getId().equals(procId)) {
                selectedProcedure.ifPresent(procedure -> Collections.swap(procedureList, 0, procedureList.indexOf(procedure)));
            }

            List<User> masterList = userRepository.findAllMastersBySpecialityId(selectedProcedure.get().getSpeciality().getId());
            Optional<User> selectedMaster = userRepository.findById(masterId);


            if (selectedMaster.isPresent()) {
                if (masterList.contains(selectedMaster.get())) {
                    selectedMaster.ifPresent(master -> Collections.swap(masterList, 0, masterList.indexOf(master)));
                } else {
                    return "redirect:/client-home/" + client.get().getId() + "/book/procedure/" + selectedProcedure.get().getId() + "/master/" + masterList.get(0).getId() + "/date/" + date;
                }
            }

            List<String> formattedList = requestService.getWorkingDays(masterId);
            List<Time> slots = requestService.getTimeSlots(masterId, date, selectedProcedure.get().getDuration());

            model.addAttribute("day", date + " 00:00:00");
            model.addAttribute("timeslots", slots);
            model.addAttribute("workDayList", formattedList);
            model.addAttribute("masterList", masterList);
            model.addAttribute("procedureList", procedureList);
            model.addAttribute("request", new RequestDto());


            model.addAttribute("request", new RequestDto());
            model.addAttribute("client", client.get());
            return "client/client-make-request";
        }
        return "error";
    }

    private Long getAuthenticatedUserId(){
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userService.findByEmail(userDetails.getUsername());
        return user.get().getId();
    }
}
