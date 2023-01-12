# picture
<img width="206" alt="image" src="https://user-images.githubusercontent.com/35441780/211566574-c88270e6-915a-4a2f-81e8-53cba6f31db5.png"> <img width="205" alt="image" src="https://user-images.githubusercontent.com/35441780/211566600-245fd0f1-4a1d-4205-8eba-628ed4275b19.png"> <img width="207" alt="image" src="https://user-images.githubusercontent.com/35441780/211566615-e469fdaf-cbae-4d08-97e5-9ac39d53f2c1.png">

설명
```
기존의 사진 앱에서나 보정 앱에서 혼자 처리하기 어려운 작업들을 간단하게 의뢰할 수 있는 서비스입니다. 
앞머리를 늘린다거나, 배경을 제거하는 등의 작업들은 어렵지 않지만, 포토샵이 없이는 처리가 어렵습니다. 
이와 같은 사진 보정이 필요할 때, 작가의 평점, 별점 등을 확인하여 원하는 작가와 채팅을 통해서 의뢰하고 결과물을 교환할 수 있습니다.
```

DB 설정 
```
CREATE USER 'picture'@'%' IDENTIFIED BY 'durtnlchrhtn@1';
CREATE USER 'picture'@'localhost' IDENTIFIED BY 'durtnlchrhtn@1';
CREATE DATABASE PICTURE;
GRANT ALL on picture.* TO 'picture'@'%' IDENTIFIED BY 'durtnlchrhtn@1';
GRANT ALL on picture.* TO 'picture'@'localhost' IDENTIFIED BY 'durtnlchrhtn@1';
FLUSH PRIVILEGES;
```

서버 실행 방법 
```
1. DB -> 테이블은 hibernate가 자동으로 생성
2. application.yml의 picture.upload-path 설정
2. mvn clean package -P dev -DskipTests 
3. java -server -Xms2g -Xmx2g -Dspring.profiles.active=dev -jar target/*.jar
```


앱 실행 방법
```
1. npm i
2. ios일 경우 cd ios && pod install 추가 실행
3. npx react-native run-android 혹은 npx react-native run-ios
```
