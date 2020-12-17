package com.springproject.beautysaloon.service.impl;

import com.springproject.beautysaloon.model.Request;
import com.springproject.beautysaloon.model.User;
import com.springproject.beautysaloon.model.WorkDay;
import com.springproject.beautysaloon.repository.RequestRepository;
import com.springproject.beautysaloon.repository.UserRepository;
import com.springproject.beautysaloon.repository.WorkDayRepository;
import com.springproject.beautysaloon.service.RequestService;
import com.springproject.beautysaloon.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final WorkDayRepository workDayRepository;
    private final UserRepository userRepository;

    public RequestServiceImpl(RequestRepository requestRepository, WorkDayRepository workDayRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.workDayRepository = workDayRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<Request> findPaginatedByPersonId(int pageNo, int pageSize, String sortField, String sortDirection, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        if(user.isPresent()){
            switch (user.get().getRole()){
                case CLIENT:
                    return requestRepository.findAllByClient_Id(pageable, userId);
                case MASTER:
                    return requestRepository.findAllByProcedureMaster(pageable, userId);
            }
        }
        return null;
    }

    @Override
    public Page<Request> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return requestRepository.findAll(pageable);
    }

    @Override
    public List<Request> findAllRequestsByMasterIdAndDate(Long id, Timestamp timestamp) {
        return requestRepository.findAllRequestsByMasterIdAndDate(id, timestamp);
    }

    @Override
    public List<Request> findAllByMasterId(Long id) {
        return requestRepository.findAllByMasterId(id);
    }

    @Override
    public Page<Request> findAllByClientId(int pageNo, int pageSize, String sortField, String sortDirection, Long id) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return requestRepository.findAllByClient_Id(pageable, id);
    }

    @Override
    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    @Override
    public Optional<Request> findById(Long id) {
        return requestRepository.findById(id);
    }

    @Override
    public List<Time> getTimeSlots(Long masterId, String timestamp, Date selectedProcedureDuration) {

        List<Request> requestList = findAllRequestsByMasterIdAndDate(masterId, Timestamp.valueOf(timestamp + " 00:00:00"));
        Time fromTime = workDayRepository.findStartTimeByMasterId(masterId, java.sql.Date.valueOf(timestamp));
        Time tillTime = workDayRepository.findEndTimeByMasterId(masterId, java.sql.Date.valueOf(timestamp));

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

        Time beforeRequestDuration;
        Time duration;
        for(Request request : requestList){
            for(int i =0; i < slots.size(); i++){
                if(request.getTime().equals(slots.get(i))){
                    duration = new Time(request.getProcedure().getDuration().getTime());
                    beforeRequestDuration = new Time(selectedProcedureDuration.getTime());
                    beforeRequestDuration.setTime(slots.get(i).getTime() - beforeRequestDuration.getTime() - TimeUnit.HOURS.toMillis(3));
                    duration.setTime(duration.getTime() + slots.get(i).getTime() + TimeUnit.HOURS.toMillis(3));

                    Time finalBeforeRequestDuration = beforeRequestDuration;
                    Time finalDuration = duration;
                    slots.removeIf(slot -> slot.getTime() >= finalBeforeRequestDuration.getTime() && slot.getTime() <= finalDuration.getTime());
                    break;
                }
            }
        }
        return slots;
    }

    public List<String> getWorkingDays(Long masterId){
        List<String> formattedList = new ArrayList<>();
        List<WorkDay> workDayList = workDayRepository.findAllByMasterId(masterId);
        Calendar calendar = Calendar.getInstance();
        for (WorkDay date : workDayList) {
            calendar.setTime(date.getDay());
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d yyyy", Locale.ENGLISH);
            formattedList.add(formatter.format(calendar.getTime()));
        }
        return formattedList;
    }

    @Override
    public void setDone(Long id) {
        requestRepository.setDone(id);
    }

    @Override
    public void deleteById(Long id) {
        requestRepository.deleteById(id);
    }
}
