package com.springproject.beautysaloon.controller;

import com.springproject.beautysaloon.dto.*;
import com.springproject.beautysaloon.model.*;
import com.springproject.beautysaloon.repository.*;
import com.springproject.beautysaloon.security.JwtTokenProvider;
import com.springproject.beautysaloon.validator.LoginValidator;
import com.springproject.beautysaloon.validator.UserValidator;
import com.springproject.beautysaloon.service.FeedbackService;
import com.springproject.beautysaloon.service.ProcedureService;
import com.springproject.beautysaloon.service.RequestService;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MainController {

    private final SpecialityRepository specialityRepository;
    private final WorkDayRepository workDayRepository;
    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final FeedbackService feedbackService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserValidator userValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginValidator loginValidator;
    private final PasswordEncoder passwordEncoder;

    public MainController(SpecialityRepository specialityRepository, WorkDayRepository workDayRepository, RequestRepository requestRepository, RequestService requestService, FeedbackService feedbackService, AuthenticationManager authenticationManager, ProcedureService procedureService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, AuthenticationManager authenticationManager1, UserService userService, UserValidator userValidator, JwtTokenProvider jwtTokenProvider1, LoginValidator loginValidator, PasswordEncoder passwordEncoder) {
        this.specialityRepository = specialityRepository;
        this.workDayRepository = workDayRepository;
        this.requestRepository = requestRepository;
        this.requestService = requestService;
        this.feedbackService = feedbackService;
        this.authenticationManager = authenticationManager1;
        this.userService = userService;
        this.userValidator = userValidator;
        this.jwtTokenProvider = jwtTokenProvider1;
        this.loginValidator = loginValidator;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/team")
    public String getTeam() {
        return "team";
    }

    @GetMapping("/service")
    public String service() {
        return "service";
    }


    @GetMapping("/home")
    @PreAuthorize(value = "hasAnyAuthority('developers:read', 'client:read', 'master:read')")
    public String getUserHomePage() {

        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().contains("master")) {
                return "redirect:/master-home";
            }
            if (authority.getAuthority().contains("developers")) {
                return "redirect:/admin-home";
            }
            if (authority.getAuthority().contains("client")) {
                return "redirect:/client-home";
            }
        }
        return "index";
    }

    @GetMapping("/client-home")
    @PreAuthorize(value = "hasAuthority('client:read')")
    public String getClientHomePage() {
        return "client/client-home";
    }

    @GetMapping("/master-home")
    @PreAuthorize(value = "hasAnyAuthority('master:read')")
    public String getMasterHomePage(Model model) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return getMasterHomePageDate(model, formatter.format(new Date()));
    }

    @GetMapping("/master-home/{date}")
    public String getMasterHomePageDate(Model model, @PathVariable(value = "date") String date) {
        float doneRequests = 0;
        org.springframework.security.core.userdetails.User masterDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> master = userService.findByEmail(masterDetails.getUsername());
        if (master.isPresent()) {

            MasterDto masterDto = MasterDto.fromUser(master.get());
            Long masterId = masterDto.getId();
            Integer rating = userService.getMasterRatingById(masterId);
            List<Request> requestListAll = requestService.findAllByMasterId(masterId);
            List<Feedback> feedbackList = feedbackService.findAllByMasterId(masterId);
            List<Request> requestListByDate = requestService.findAllRequestsByMasterIdAndDate(masterId, Timestamp.valueOf(date += " 00:00:00"));
            List<WorkDay> workDayList = workDayRepository.findAllByMasterId(masterId);
            List<String> formattedList = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            for (WorkDay day : workDayList) {
                calendar.setTime(day.getDay());
                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d yyyy", Locale.ENGLISH);
                formattedList.add(formatter.format(calendar.getTime()));
            }

            WorkDay selectedWorkDay = workDayRepository.findWorkDayByDayAndMasterId(java.sql.Date.valueOf(date.substring(0, 10)), masterId);
            if (selectedWorkDay == null) {
                selectedWorkDay = workDayRepository.findAllByMasterId(masterId).get(0);
            }
            if (selectedWorkDay != null) {
                Collections.swap(workDayList, 0, workDayList.indexOf(selectedWorkDay));
            }

            List<User> userList = requestRepository.findAllClientsByMasterId(masterId);

            for (Request request : requestListByDate) {
                if (request.isDone()) {
                    doneRequests++;
                }
            }
            double percentage = 0;
            if (!requestListByDate.isEmpty()) {
                percentage = (doneRequests * 100) / requestListByDate.size();
            }

            model.addAttribute("clientSize", userList.size());
            model.addAttribute("workDayList", formattedList);
            model.addAttribute("date", selectedWorkDay);
            model.addAttribute("feedbackList", feedbackList.size());
            model.addAttribute("requestListTotalSize", requestListAll.size());
            model.addAttribute("requestListByDate", requestListByDate);
            model.addAttribute("master", masterDto);
            model.addAttribute("rating", rating);
            model.addAttribute("masterName", master.get().getName());
            model.addAttribute("pass", percentage);
            model.addAttribute("fail", 100 - percentage);

        }

        return "master/master-home";
    }

    @GetMapping("/admin-home")
    @PreAuthorize(value = "hasAnyAuthority('developers:read')")
    public String getAdminHomePage(Model model) {
        org.springframework.security.core.userdetails.User masterDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> master = userService.findByEmail(masterDetails.getUsername());
        if (master.isPresent()) {
            AdminUserDto admin = AdminUserDto.fromUser(master.get());
            model.addAttribute("name", admin.getName());
        }
        return "admin/admin-home";
    }

    @GetMapping("/admin-home/{role}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String getAllEntities(Model model, @PathVariable(value = "role") String role) {
        return findPaginatedUsers(1, "name", "asc", model, role);
    }

    @GetMapping("/admin-home/{role}/page/{pageNo}")
    @PreAuthorize("hasAuthority('developers:read')")
    public String findPaginatedUsers(@PathVariable(value = "pageNo") int pageNo,
                                     @RequestParam("sortField") String sortField,
                                     @RequestParam("sortDirection") String sortDirection,
                                     Model model, @PathVariable(value = "role") String roleStr) {
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


    @GetMapping("/admin-home/masters/new/master")
    @PreAuthorize("hasAuthority('developers:read')")
    public String getForm(Model model) {
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
    public String saveMaster(@Valid @ModelAttribute("master") MasterDto masterDto, Errors errors, BindingResult result) {
        if (errors.hasErrors()) {
            return "user/add-user-form";
        }
        List<Speciality> specialityList = new ArrayList<>();
        for (Speciality specialityStr : masterDto.getSpecialityList()) {
            specialityList.add(specialityRepository.getOne(specialityStr.getId()));
        }
        User master = masterDto.toUser();
        master.setSpecialityList(specialityList);
        master.setPassword(passwordEncoder.encode(masterDto.getPassword()));
        userService.saveUser(master);
        return "redirect:/admin-home/masters";
    }

    @GetMapping("/admin-home/delete/{role}/{id}")
    @PreAuthorize(value = "hasAuthority('developers:write')")
    public String deleteUser(@PathVariable(value = "role") String roleStr,
                             @PathVariable(value = "id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin-home/" + roleStr;
    }

}
