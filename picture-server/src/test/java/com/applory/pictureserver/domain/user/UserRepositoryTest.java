package com.applory.pictureserver.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    public void saveUser() {


        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setOfficialPrice(i);
            userRepository.save(user);
        }

        // 트랜젝션 안에서 머지를 하면 값이 안바뀌었으면 안바꾸나보다.

        // save를 하면 persistent context에만 들어가고
        // tx.commit()을 해야 실제로 db에 등록이 된다


    }
}
