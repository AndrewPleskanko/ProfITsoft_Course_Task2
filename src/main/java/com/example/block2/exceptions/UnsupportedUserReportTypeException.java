package com.example.block2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.block2.enums.UserReportType;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UnsupportedUserReportTypeException extends BaseException {

    public UnsupportedUserReportTypeException(UserReportType reportType) {
        super("Unsupported user report type: " + reportType);
    }
}
