#  **FIFTYFIFTY FAN PAGE (Vue3.0 + REST API 프로젝트)**

<h3>K-POP 그룹 FIFTYFIFTY의 팬 메이드 페이지입니다.</h3>

![localhost_8081_ (1)](https://github.com/user-attachments/assets/14c9485e-d60f-48f0-bcea-bbdfc4fc0b9b)



# 📚 **프로젝트 목차**

### 1. 프로젝트 소개
### 2. 프로젝트 ERD
### 3. 학습포인트와 보완점

# :gear: 1. **프로젝트 개요**

### :bulb: 팀원                 
- 1명                             

### :bulb: 프로젝트 기간
- 2025-05-14 ~ 진행중
### :bulb: 기술 스택
- Vue 3, TypeScript, Spring Boot, MyBatis, MySQL, Axios, FullCalendar.js, Chart.js 등
### :bulb: 배포 환경
- Front - Netlify / Back - Render, Docker / 스토리지 - AWS S3 / CDN - AWS CloudFront / RDS - AWS RDS(MySQL)

### :bulb: 주요기능

 **테마 전환**
![2025-06-05_18-21-45](https://github.com/user-attachments/assets/2fd657ba-4973-4c51-837c-bfaf5a9a0fd3)
  <p>
    로고, 배경 아이콘을 모두 테마별 리소스로 분리
  </p>
  ### :bulb: 애로사항
  <p>
    배포시 이미지 불러오는 과정에서 버벅거림 발생 
  </p>
  <p>
    기존 transition으로 이미지를 가져오는 방식에서 이미지 두개를 미리 로드해두고 opacity로 이상없이 화면에 나오도록 구현
  </p>

![bandicam 2025-05-31 02-01-34-160](https://github.com/user-attachments/assets/72247bc7-d7ef-4bc8-a875-a92c9b9160e9)

  <p>하나의 페이지로 구현되는 각 기능들을 독립된 컴포넌트로 분리하여  재사용성과 유지보수성을 높였습니다.
      또한 함수 및 변수의 이름이 역할이 드러나도록 설계해 기능 탐색의 편의성을 향상시키고 TypeScript를 사용하여 잘못된 정보가 들어가지 않도록 설계하였습니다.</p>
<br><br><br>

 **카테고리 별 일정 캘린더**
![2025-06-05_18-21-45_2](https://github.com/user-attachments/assets/9e1d4958-d4b6-45a6-9156-4d9a4c39e0ec)
![bandicam 2025-05-31 01-12-50-787](https://github.com/user-attachments/assets/ac35d3c0-71d4-4038-8398-f81d8a4260d4)

  <p>
음방, 컴백 등 crt 필드를 가진 이벤트 컬렉션 FullCalendar.js 사용 및 카테고리 별 색상 표시

일정 데이터를 조회하기 전, 백엔드에서 crt 필드를 기준으로 각 일정의 카테고리를 구분하고, 프론트에는 색상 정보와 함께 전달되도록 하여 사용자가 일정을 직관적으로 구분할 수 있도록 했습니다.
  </p>

<br><br><br>

  ### :bulb: 애로사항
  <p>
    Fullcalendar 커스터마이징으로 모바일 반응형 한계
  </p>
  <p>
    모바일에서는 보다 최적화된 Vue-cal 활용해서 pc 화면에서는 Fullcalendar, 모바일에서는 Vue-cal로 구현
  </p>


  <br><br><br>
 **유튜브 TOP 10 자동 업데이트**
 ![2025-06-05_18-21-45_4](https://github.com/user-attachments/assets/066c9aa2-352e-4d72-9b55-dbe9803fad7c)

  <p>
    @Scheduled과 Cron을 사용해 매일 오전 9시마다 피프티로 영상 검색 후 
    필터 처리 후 DB에 탑 10 영상 썸네일, 주소, view, 제목 저장 및 조회
  </p>

   ### :bulb: 애로사항
  <p>
    Render 서버 무료버전으로 서버 슬립 상태로 돌입하여 스케줄이 동작하지 않는 현상 발생
  </p>
  <p>
    1. Git Action을 활용해 healthz http 메소드에 요청을 10분마다 전달하도록 설정 -> Git action이 정상적으로 동작하지 않음
    2. 외부 페이지인 UpdateRobot 페이지를 활용해 10분마다 요청 전달 이후 서버 슬립 없이 동작 확인인
  </p>

<br><br><br>

![bandicam 2025-05-31 01-24-48-185](https://github.com/user-attachments/assets/e5666ea9-da88-41b4-bcf9-7bceb1e4cd6b)
![bandicam 2025-05-31 01-24-56-994](https://github.com/user-attachments/assets/cbab4beb-0e5a-4b76-90a5-c5b143749cc2)


<p>Spring에서 제공하는 @Scheduled 기능을 활용하여 매일 오전 9시 마다 Youtube API를 활용해 조회수 탑 10개 영상을 가져오도록 설계했으며 필터처리를 통해 특정 날짜 이후 특정 단어를 제외한 검색기록만 가져오도록 설계하여 잘못된 정보가 들어오지 않도록 구현하였습니다.</p>

<p>ThreadPoolTaskScheduler를 사용하여 스케줄러 스레드가 정해진 개수막 동작하도록 설정했으며 @Transactional을 활용하여 잘못된 정보가 저장되지 않도록 설계</p>

- **Today 체크 및 시각화**
![bandicam 2025-06-25 12-39-45-676](https://github.com/user-attachments/assets/897758f6-cb76-4351-83e7-3b79ea5f7529)
 <p>
  페이지 접속시 today를 체크해 카운팅 진행 후 23:59까지 유효한 쿠키를 생성해 신규/재방문 유입을 체크해 Chart.js를 통해 시각화를 진행했습니다.
   해당 기능을 통해 어떤 날 유입이 가장 많은지 파악하며 상황에 유동적으로 대처할수 있습니다.
 </p>

   ### :bulb: 애로사항
  <p>
    배포 이후 기존 back-end에서 진행하던 투데이 쿠키 저장이 동작하지 않는 현상 발생
  </p>
  <p>
   기존 back-end에서 저장하던 쿠키를 front-end에서 바로 저장하고 전달해 확인하는 방식으로 변경
  </p>

- **DB 데이터 자동 삭제**
- ![bandicam 2025-06-10 16-41-38-923](https://github.com/user-attachments/assets/c3c66b36-d21d-4827-b1ba-4e5d72315060)
![bandicam 2025-06-10 16-41-51-029](https://github.com/user-attachments/assets/4e437ecd-2718-4e02-9462-1841d31e8d38)
  <p>
    DB에 이미지를 체크하여 DB에는 있지만 경로에 이미지가 없는 경우 DB를 자동으로 삭제하도록 구현하여 불필요한 용량이 차지하지 않도록 설계하였습니다.
  </p>

  - **이미지 갤러리**
  - ![2025-06-05_18-21-45_5](https://github.com/user-attachments/assets/0dbe44f9-e9f5-457f-acad-2951ebff93c2)
![2025-06-05_18-21-45_6](https://github.com/user-attachments/assets/b8af6da0-5c44-4e52-b681-b595805802b2)

  <p>
    backend 에서 페이징 처리를 진행하여 접속시 과도한 리소스 방지
  </p>

   ### :bulb: 애로사항
  <p>
    페이징 및 필터 이동시 마다 끊기는 현상 발생
  </p>
  <p>
    페이지 접속 시 모든 데이터를 가져오고 Front-end에서 페이징 및 필터 처리를 진행해 유저 사용성 높임  </p>

![bandicam 2025-05-31 01-24-48-185](https://github.com/user-attachments/assets/e5666ea9-da88-41b4-bcf9-7bceb1e4cd6b)
![bandicam 2025-05-31 01-24-56-994](https://github.com/user-attachments/assets/cbab4beb-0e5a-4b76-90a5-c5b143749cc2)


<p>Spring에서 제공하는 @Scheduled 기능을 활용하여 매일 오전 9시 마다 Youtube API를 활용해 조회수 탑 10개 영상을 가져오도록 설계했으며 필터처리를 통해 특정 날짜 이후 특정 단어를 제외한 검색기록만 가져오도록 설계하여 잘못된 정보가 들어오지 않도록 구현하였습니다.</p>

<p>ThreadPoolTaskScheduler를 사용하여 스케줄러 스레드가 정해진 개수막 동작하도록 설정했으며 @Transactional을 활용하여 잘못된 정보가 저장되지 않도록 설계</p>

- **Today 체크 및 시각화**
![bandicam 2025-06-25 12-39-45-676](https://github.com/user-attachments/assets/897758f6-cb76-4351-83e7-3b79ea5f7529)
 <p>
  페이지 접속시 today를 체크해 카운팅 진행 후 23:59까지 유효한 쿠키를 생성해 신규/재방문 유입을 체크해 Chart.js를 통해 시각화를 진행했습니다.
   해당 기능을 통해 어떤 날 유입이 가장 많은지 파악하며 상황에 유동적으로 대처할수 있습니다.
 </p>

   ### :bulb: 애로사항
  <p>
    배포 이후 기존 back-end에서 진행하던 투데이 쿠키 저장이 동작하지 않는 현상 발생
  </p>
  <p>
   기존 back-end에서 저장하던 쿠키를 front-end에서 바로 저장하고 전달해 확인하는 방식으로 변경
  </p>

- **DB 데이터 자동 삭제**
- ![bandicam 2025-06-10 16-41-38-923](https://github.com/user-attachments/assets/c3c66b36-d21d-4827-b1ba-4e5d72315060)
![bandicam 2025-06-10 16-41-51-029](https://github.com/user-attachments/assets/4e437ecd-2718-4e02-9462-1841d31e8d38)
  <p>
    DB에 이미지를 체크하여 DB에는 있지만 경로에 이미지가 없는 경우 DB를 자동으로 삭제하도록 구현하여 불필요한 용량이 차지하지 않도록 설계하였습니다.
  </p>

  - **이미지 갤러리**
  - ![2025-06-05_18-21-45_5](https://github.com/user-attachments/assets/0dbe44f9-e9f5-457f-acad-2951ebff93c2)
![2025-06-05_18-21-45_6](https://github.com/user-attachments/assets/b8af6da0-5c44-4e52-b681-b595805802b2)

  <p>
    backend 에서 페이징 처리를 진행하여 접속시 과도한 리소스 방지
  </p>

   ### :bulb: 애로사항
  <p>
    페이징 및 필터 이동시 마다 끊기는 현상 발생
  </p>
  <p>
    페이지 접속 시 모든 데이터를 가져오고 Front-end에서 페이징 및 필터 처리를 진행해 유저 경험 개선선
  </p>



  - ** 유튜브 최신 영상 가져오기**

  ![bandicam 2025-06-25 12-50-55-924](https://github.com/user-attachments/assets/708ff4e1-f6dc-4a2f-9ea0-0ce35565a827)
![bandicam 2025-06-25 12-47-19-677](https://github.com/user-attachments/assets/578622be-c30c-469b-be75-8df24435a106)

YouTube API 를 활용하여 최신영상을 가져오도록 설정했습니다. 또한 특정 키워드로 반복 진행해 잘못된 정보가 들어오지않고 원하는 정보가 들어오도록 필터링 처리하였습니다. (특정키워드, 구독자수, 필터링 키워드 등)



- **어드민 페이지**
- ![bandicam 2025-05-30 19-11-03-897](https://github.com/user-attachments/assets/fab41c6c-3870-421e-be31-4bc9b10ef3a7)

  <p>
    Spring Security와 JWT 토큰을 활용한 로그인 기능 구현해 ADMIN 권한이 있는 유저만 접속 가능
  </p>


** Swagger api**
![image](https://github.com/user-attachments/assets/c7cee12f-8471-47f5-85dd-326728e93869)


서버 문제 발생시 비즈니스로직 확인을 위해 swagger api를 활용해 문서화 진행
  

### :bulb: 사용 기술
<p>사용 언어 : HTML5, CSS3, Java, SQL, TypeScript(5.0v) </p>

<p>프론트엔드 : Vue.js 3.0, Axios(1.9v), fullCalendar.js(6.1.17v), SweetAlert(11.22v), Chart.js </p>

<p>백엔드 : Spring Boot(3.5.0v), Spring Security, MyBatis(3.0.4v), Lombok, JWT(0.12.6v), Springdoc(2.7v), Cron(@Scheduled)</p>

<p>DB : MySQL</p>

<p>서버 런타임 환경 : Node.js(npm 10.9.2v)</p>

<p>개발도구 : isual Studio Code, MySQL Workbench, Spring Boot Devtools</p>

<p>협업툴 : GitHub, ERDCloud </p>


<p>배포환경 : Netlify, Render, AWS S3, AWS CloudFront, AWS RDS, Docker </p>


### :bulb: ⚙ 프로젝트 설계 ERD
![bandicam 2025-06-25 14-43-47-074](https://github.com/user-attachments/assets/45dda539-4e7b-4e3c-a912-0a816c89c0b9)


### :bulb: 트러블슈팅
1. IllegalStateException 에러발생
   에러원인 Spring Boot Devtools 기능 중 라이브 리로딩 진행시 컨텍스트를 종료후 시작하는 과정에서 Mybatis 메퍼 파일 로드하려고해서 발생하는 오류

   보통 Mybatis 환경설정 문제, 데이터베이스 연결 문제, Mybatis 클래스 패스 충돌 문제, IDE 캐시 문제로 발생

   하지만 스케줄 등록 이전까지는 이상없이 라이브 리로딩이 진행된 만큼 스케줄 부분에 문제가 있다고 판단 (스케줄이 종료 이후에도 스레드가 남아서 서버 과부하 우려)

   방법1: sprong.task.scheduling.shutdown.await-termination=true 설정해 스케줄링 작업 완료 후 동작하게끔 설정 : 에러발생
   방법2: IDE 캐시 제거 및 재설치 : 에러발생
   방법3: @EnableAsync, @Async 를 사용해 스레드 풀을 관리하고, 종료 시 대기 시간을 설정 : 에러발생
   방법3번 설정 이후 3분 단위로 스케줄 동작 스케줄이 돌고있을때는 한번더 돌지 않고 모든 작업이 끝난 후 동작하는거 확인
   방법4: 스케줄 기능 제거 : 에러발생

   방법4로 인해 스케줄로 인한 에러가 아니라고 판단 Devtools 제거 이후에는 서버 이상없이 동작 운영에는 큰 지장없는 에러 : 임시방편

2. java.io.EOFException: null 에러발생
   데이터를 읽어오는 과정에서 예상치못하게 파일 끝에 도달 했을때 발생하는 에러
   Render같은 경우 60초 내에 서버를 실행해야하는데 서버 실행이 90초이상 걸려 발생하는 에러라 판단

   방법1: AWS RDS서버는 서울이지만 Render 서버는 북미인걸 확인 북미 서버 > 싱가포르 서버로 변경 이후 93초 > 77초로 단축
   방법2: 지연 초기화를 활성화 spring.main.lazy-initialization=true 와 HikariCP 커넥션 풀 설정을 커스터마이징해 서버 런타임 총 93초 > 43초로 단축해 이상없이 배포를 완료했습니다.
   

### :bulb: 학습포인트
- 컴포넌트 단위 분리와 TypeScript로 안전한 코드 작성
- Spring Boot에서 @Scheduled를 이용한 스케줄러 구현
- JWT 토큰 기반 인증 및 권한 관리
- REST API 문서화 및 테스트 자동화(Swagger)
- ThreadPoolTaskScheduler를 활용한 스레드 커스터마이징
- DB 자동 정리 로직 설계 및 구현
- Chart.js를 활용한 데이터 시각화 구현
- YouTube api를 활용해 오픈 api 연동
- Netlify, Render, AWS S3, AWS CloudFront, AWS RDS로 배포하고 Git Action 을 활용해 CI/CD 전반적인 관리 경험

### :bulb: ⚙ 프로젝트 보완 및 아쉬운점
- WebSocket을 활용해 캘린더 일정이 다가올 경우 실시간으로 페이지 알람 요청 예정
- Redis 캐시를 활용한 속도 향상 및 효율 증진 작업 필요
- **Papago API를 활용한 다국어 번역 기능 추가 예정**

