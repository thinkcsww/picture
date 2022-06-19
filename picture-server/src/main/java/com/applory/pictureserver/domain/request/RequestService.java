package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.domain.shared.SecurityUtils;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    public Request createRequest(RequestDto.Create dto) {
        Request request = new Request();
        request.setRequestType(dto.getRequestType());
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setDueDate(dto.getDueDate());
        request.setDesiredPrice(dto.getDesiredPrice());
        request.setMatchYN("N");
        request.setReadCount(0);

        // TODO - authentication principal로 가져올 수 있는지 알아보기
        User user = userRepository.findByUsername(SecurityUtils.getPrincipal());
        request.setUser(user);

        return requestRepository.save(request);
    }

    public Page<Request> getRequests(RequestDto.Search search, Pageable pageable) {
        return requestRepository.findRequestBySearch(search, pageable);
    }
}
