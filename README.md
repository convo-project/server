<div align="center">
<a href="https://convochat.site">
<img width="250px" src="https://github.com/user-attachments/assets/5da6ae9d-f055-45cf-9205-bd9dbeb1097b" alt="콘보"/>
</a>

# CONVO

### ✉늘 새롭고 유익한 IT 랜덤채팅, CONVO✉

</div>

취업을 준비하면서 나와 같은 길을 걷는 사람들과 이야기를 나누고 싶었던 적 있으신가요? 어디서 정보를 얻어야 할지 막막했던 경험이 있으신가요?

`CONVO`는 IT 직군에 관심 있는 사람들과 자유롭게 대화할 수 있는 랜덤채팅 플랫폼입니다.
같은 직무를 준비하는 사람들과 실시간으로 소통하고, 생생한 정보와 인사이트를 나누며, 새로운 기회를 만들어 보세요!

<br/>

## 👻 Member

<table>
<tr>
<td align="center"> 프론트엔드</td>
<td align="center"> 백엔드</td>
</tr>
  <tr>
    <td align="center">
      <a href="https://github.com/imkrmin" target="_blank">
        임주민
      </a>
    </td>
     <td align="center">
      <a href="https://github.com/qudwns017" target="_blank">
       최병준
      </a>
  </tr>
</table>

## 🛠️ Skills

### 백엔드
<img width="700px" src='https://github.com/user-attachments/assets/844658c4-d9b7-4b70-9aab-1d1fb8a8b4f1'  alt="Skills"/>

### 인프라
<img width="700px" src='https://github.com/user-attachments/assets/c267cccd-7017-4c09-b127-60264ae2cad4'  alt="Skills"/>

## ⚙️ 애플리케이션 아키텍처
<img width="700px" src='https://github.com/user-attachments/assets/4083b766-49be-4e6a-b272-bf55364e55ae'  alt="Skills"/>

### 요청 흐름
① User는 브라우저(Chrome, Safari 등)를 실행한다.<br>
② 브라우저를 통해 ColudFront와 S3로 배포된 Client에 접속한다.<br>
③ Client는 Server에 API를 요청한다.<br>
④ Let's Encrypt 인증서를 사용한 Nginx를 통해 SSL 인증서 기반 암호화된 HTTPS 통신을 수행한다.<br>
  서버의 도메인으로 들어온 request는 프록시 패스를 통해 Spring이 실행 중인 포트 번호로 전달된다.<br>
⑤ 컨테이너 내부 네트워크를 통해 Redis에 Data를 읽고 쓴다.<br>
⑥ 컨테이너 내부 네트워크를 통해 Postgresql에 Data를 읽고 쓴다.<br>
⑦ OAuth 로그인 Request인 경우, Google의 Server를 통해 리소스를 획득한다.<br>

### CI/CD 흐름
① Notion을 통해 Github의 Issue 및 PR을 가져오고, 개발자 간 문서가 공유된다.<br>
② 개발자는 작성된 코드를 Main branch에 push 한다.<br>
③ Main branch에 push가 감지되면 Webhook이 작동하여 Jenkins에 요청을 보낸다.<br>
④ Jenkins는 GitHub Repository를 Clone 하여 Spring 프로젝트를 빌드하고, Docker Hub에 빌드된 이미지를 push한다.<br>
⑤ 빌드가 성공적으로 완료되면, Jenkins는 SSH를 통해 Prod Server에 접근하고 Docker Compose를 통해 도커 이미지를 최신화 한다.<br>

## 🛠 트러블 슈팅

### 1. Jenkins 서버 부하 문제
#### 문제 상황
Jenkins를 이용하여 CI/CD 파이프라인을 구축했으나, 하나의 인스턴스에서 여러 작업이 동시에 이루어지면서 서버 부하 문제가 발생<br>
Jenkins를 통해 빌드와 배포 작업을 수행하면서 DB와 Spring 애플리케이션이 동시에 실행되어 속도가 느려지거나 메모리 부족으로 인해 서버가 종료되는 상황이 발생

#### 해결 방법
서버 부하를 줄이기 위해 Jenkins를 별도의 Deploy 서버로 분리<br>
별도의 인스턴스로 운영 서버와 배포 서버를 분리함으로써 서버 부하를 효과적으로 분산<br>
이로 인해 애플리케이션 성능 저하와 메모리 부족 문제를 해결

### 2. 토큰 저장 방식 문제
#### 문제 상황
Access Token(AT)을 쿠키나 로컬 스토리지에 저장하여 관리하면 보안적인 취약점이 발생. AT를 안전하게 관리하기 위해 HttpOnly와 Secure 속성을 true로 설정하면 보안성은 강화되지만, 클라이언트 측에서 로그인 상태 관리 불가능

#### 해결 방법
토큰을 클라이언트 메모리 변수인 상태에 저장하여 보안성 향상
페이지를 새로고침하면 해당 변수나 상태가 초기화되어 토큰이 소실되는 문제가 발생하지만 HttpOnly Cookie에 저장한 RT를 서버측에서 파싱하여 AT 재발급을 통해 해결

### 3. OAuth 계정과 기본 계정 충돌 문제
#### 문제 상황

#### 해결 방법

### 4. Filter 내 Exception 처리 문제
#### 문제 상황

#### 해결 방법
