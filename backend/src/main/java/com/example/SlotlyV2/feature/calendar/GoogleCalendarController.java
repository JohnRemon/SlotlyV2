package com.example.SlotlyV2.feature.calendar;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.DataResponse;
import com.example.SlotlyV2.feature.calendar.dto.ConnectResponse;
import com.example.SlotlyV2.feature.calendar.dto.ConnectionStatus;
import com.example.SlotlyV2.feature.calendar.dto.ExchangeRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/calendar/google")
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarController {

    private final GoogleCalendarConnectionService connectionService;

    @GetMapping("/connect")
    public DataResponse<ConnectResponse> initiateConnection(HttpServletRequest request) {
        return DataResponse.of(new ConnectResponse(connectionService.initiateConnection(request)));
    }

    @PostMapping("/exchange")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exchangeAuthorizationCode(
            @Valid @RequestBody ExchangeRequest request,
            HttpServletRequest httpRequest) {
        connectionService.exchangeCode(request.getCode(), request.getState(), httpRequest);
    }

    @DeleteMapping("/disconnect")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disconnect() {
        connectionService.disconnect();
    }

    @GetMapping("/status")
    public DataResponse<ConnectionStatus> getConnectionStatus() {
        return DataResponse.of(connectionService.getConnectionStatus());
    }
}
