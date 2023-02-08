package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.shared.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
1@Transactional
public class RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    public RequestService(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public RequestDto.VM createRequest(RequestDto.Create dto) {
        if (dto.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invalid DueDate: DueDate is past");
        }

        Request request = new Request();
        request.setSpecialty(dto.getSpecialty());
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setDueDate(dto.getDueDate());
        request.setDesiredPrice(dto.getDesiredPrice());
        request.setMatchYN(dto.getMatchYn() != null ? dto.getMatchYn() : "N");
        request.setCompleteYN(dto.getCompleteYn() != null ? dto.getCompleteYn() : "N");
        request.setReadCount(0);
        request.setChatCount(0);

        String username = SecurityUtils.getPrincipal();
        User user = userRepository.findByUsername(username);
        request.setUser(user);

        Request save = requestRepository.save(request);
        return new RequestDto.VM(save);
    }

    @Transactional(readOnly = true)
    public Page<RequestDto.VM> getRequests(RequestDto.Search search, Pageable pageable) {
        return requestRepository.findRequestBySearchQ(search, pageable).map(RequestDto.VM::new);
    }

    public RequestDto.VM getRequest(UUID id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Request not exists: " + id));
        request.setReadCount(request.getReadCount() + 1);

        RequestDto.VM requestVM = new RequestDto.VM(request);

        setAnotherRequest(id, requestVM);

        setAcceptRate(requestVM);

        return requestVM;

    }

    private void setAcceptRate(RequestDto.VM requestVM) {
        int completeCount = 0;
        int closedCount = 0;

        List<Request> usersAllRequests = requestRepository.findByUser_Id(requestVM.getUserId());
        for (Request r : usersAllRequests) {
            if ("Y".equals(r.getCompleteYN()) || ("Y".equals(r.getMatchYN()) && r.getDueDate().isBefore(LocalDateTime.now()))) {
                closedCount++;
            }
            if ("Y".equals(r.getCompleteYN())) {
                completeCount++;
            }
        }

        if (completeCount != 0 && closedCount != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#.0");
            double acceptRate = (double) completeCount / closedCount * 100.0;
            double formattedAcceptRate = Double.parseDouble(decimalFormat.format(acceptRate));
            requestVM.setUserAcceptRate(formattedAcceptRate);
        } else {
            requestVM.setUserAcceptRate(-1.0);
        }
    }

    private void setAnotherRequest(UUID id, RequestDto.VM requestVM) {
        RequestDto.Search search = new RequestDto.Search();
        search.setUserId(requestVM.getUserId());
        search.setExceptThisId(id);

        Page<Request> anotherRequests = requestRepository.findRequestBySearchQ(search, PageRequest.of(0, 4, Sort.Direction.ASC, "dueDate"));
        requestVM.setAnotherRequests(anotherRequests.getContent().stream().map(RequestDto.VM::new).collect(Collectors.toList()));

        // Request의 주인에게 다른 Request가 없으면 랜덤한 다른 Reqeust를 가져온다.
        if (anotherRequests.isEmpty()) {
            RequestDto.Search secondSearch = new RequestDto.Search();
            secondSearch.setExceptThisId(id);

            Page<RequestDto.VM> secondAnotherRequests = requestRepository.findRequestBySearchQ(secondSearch, PageRequest.of(0, 4, Sort.Direction.ASC, "dueDate"))
                            .map(RequestDto.VM::new);
            requestVM.setAnotherRequests(secondAnotherRequests.getContent());
        }
    }
}
