package com.springproject.beautysaloon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springproject.beautysaloon.model.Procedure;
import com.springproject.beautysaloon.model.Request;
import com.springproject.beautysaloon.model.User;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestDto {

    private Long id;

    private Procedure procedure;
    private User master;
    private String date;
    private Time time;
    private User client;
    private boolean done;

    public Request toRequest(){
        Request request = new Request();
        request.setId(id);
        request.setProcedure(procedure);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            request.setDate(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        request.setTime(time);
        request.setClient(client);
        request.setMaster(master);
        request.setDone(done);
        return request;
    }

    public static RequestDto fromRequest(Request request){
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setProcedure(request.getProcedure());
        requestDto.setMaster(request.getMaster());
        requestDto.setClient(request.getClient());
        requestDto.setDate(String.valueOf(request.getDate()));
        requestDto.setTime(request.getTime());
        requestDto.setDone(request.isDone());
        return requestDto;
    }


}
