package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.exception.NotFoundException;
import com.applory.pictureserver.shared.SecurityUtils;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
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

        String username = SecurityUtils.getPrincipal();
        User user = userRepository.findByUsername(username);
        request.setUser(user);

        Request save = requestRepository.save(request);
        return new RequestDto.VM(save);
    }

    public Page<RequestDto.VM> getRequests(RequestDto.Search search, Pageable pageable) {
        return requestRepository.findRequestBySearchQ(search, pageable).map(RequestDto.VM::new);
    }

    @Transactional
    public RequestDto.VM getRequest(UUID id) {
        Optional<Request> optionalRequest = requestRepository.findById(id);

        if (optionalRequest.isPresent()) {
            Request request = optionalRequest.get();
            request.setReadCount(request.getReadCount() + 1);

            RequestDto.VM requestVM = new RequestDto.VM(request);

            setAnotherRequest(id, requestVM);

            requestVM.setChatCount(0);

            int completeCount = 0;
            int closedCount = 0;

            List<Request> usersAllRequests = requestRepository.findByUser_Id(requestVM.getUserId());
            for (Request r: usersAllRequests) {
                if ("Y".equals(r.getCompleteYN()) || ("Y".equals(r.getMatchYN()) && r.getDueDate().isBefore(LocalDateTime.now()))) {
                    closedCount++;
                }
                if ("Y".equals(r.getCompleteYN())) {
                    completeCount++;
                }
            }

            if (completeCount != 0 && closedCount != 0) {
                DecimalFormat decimalFormat = new DecimalFormat("#.0");
                double acceptRate = (double)completeCount / closedCount * 100.0;
                double formattedAcceptRate = Double.parseDouble(decimalFormat.format(acceptRate));
                requestVM.setUserAcceptRate(formattedAcceptRate);
            } else {
                requestVM.setUserAcceptRate(-1.0);
            }

            return requestVM;

        } else {
            throw new NotFoundException("Request not exists: " + id);
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

            Page<Request> secondAnotherRequests = requestRepository.findRequestBySearchQ(search, PageRequest.of(0, 4, Sort.Direction.ASC, "dueDate"));
            requestVM.setAnotherRequests(secondAnotherRequests.getContent().stream().map(RequestDto.VM::new).collect(Collectors.toList()));
        }
    }
}
