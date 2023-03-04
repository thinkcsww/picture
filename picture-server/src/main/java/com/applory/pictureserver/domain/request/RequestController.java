package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.shared.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRequest(@Valid @RequestBody RequestDto.Create dto) {
        requestService.createRequest(dto);
    }

    @GetMapping("")
    public Page<RequestDto.VM> getRequests(RequestDto.Search search, Pageable pageable) {
        return requestService.getRequests(search, pageable);
    }

    @GetMapping("/{id}")
    public Result getRequest(@PathVariable String id) {
        return Result.success(requestService.getRequest(id));
    }
}
