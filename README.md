# AtoZ

###Video &amp; Audio Sharing SNS Service in Android Application

###사용자들이 자신의 영상이나 음성을 자유롭게 업로드 하고 다른 사용자들과 공유할 수 있는 어플리케이션 제작 프로젝트

* 개발에 사용된 모든 이미지에 저작권 문제가 있을 경우에는 글을 내리도록 하겠습니다.

## 사용 기술
```bash
1. Client : Java - Android Studio
- Fragment Management
- Retrofit 2
- Picasso
- Google Firebase Login
```
```bash
2. Server : Node js - Atom
- Multipart
```
```bash
3. DataBase : MongoDB - Robo 3T
- Node js 연동
```
## 기본 기능
![atoz_1](https://user-images.githubusercontent.com/22411296/74808760-f14a4980-532e-11ea-80b2-3f344940391e.JPG)

(1) 메인화면 : 업로드 된 전체 게시글들을 볼 수 있는 화면
- 게시글을 클릭하면 썸네일 크기와 같은 크기로 게시글 확인 가능
- 게시글을 길게 클릭하면 (3)과 같은 세부화면에서 게시글 재생 가능

(2) 업로드 화면 : 게시글을 업로드할 수 있는 화면(영상/음성 둘 다 가능)

(3) 세부 화면 : 게시글을 세부적으로 크게 볼 수 있는 화면
- 게시글의 세부 정보와 "좋아요", "앨범 추가", "다운로드" 기능 등을 추가적으로 제공

(4) 사용자 화면 : 사용자가 업로드한 게시글을 볼 수 있는 화면
- 메인 화면과 마찬가지로 사용자가 업로드한 게시글의 썸네일을 클릭하며 해당 게시글의 세부화면 재생
