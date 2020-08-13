# AtoZ(1인 개발)

##### Video &amp; Audio Sharing SNS Service in Android Application
##### 킬링 타임을 위해 간편하게 다양한 영상 / 음성 게시글들을 업로드하고 여러 사람들과 함께 즐길 수 있는 어플리케이션
###### 개발에 사용된 모든 이미지에 저작권 문제가 있을 경우에는 글을 내리도록 하겠습니다.

### 사용 기술
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
- Mongoose
- Multer
- Multipart
```
```bash
3. DataBase : MongoDB - Robo 3T
- Node js 연동
```
### 기본 기능
![atoz_1](https://user-images.githubusercontent.com/22411296/74808760-f14a4980-532e-11ea-80b2-3f344940391e.JPG)

```bash
(1) 메인화면 : 업로드 된 전체 게시글들을 볼 수 있는 화면
- 게시글을 클릭하면 썸네일 크기와 같은 크기로 게시글 확인 가능
- 게시글을 길게 클릭하면 (3)과 같은 세부화면에서 게시글 재생 가능
```
```bash
(2) 업로드 화면 : 게시글을 업로드할 수 있는 화면(영상/음성 둘 다 가능)
```
```bash
(3) 세부 화면 : 게시글을 세부적으로 크게 볼 수 있는 화면
- 게시글의 세부 정보와 "좋아요", "앨범 추가", "다운로드" 기능 등을 추가적으로 제공
```
```bash
(4) 사용자 화면 : 사용자가 업로드한 게시글을 볼 수 있는 화면
- 메인 화면과 마찬가지로 사용자가 업로드한 게시글의 썸네일을 클릭하며 해당 게시글의 세부화면 재생
```

### 구현 내용
#### 1. 로그인 화면
![캡처](https://user-images.githubusercontent.com/22411296/90140077-31f1bf80-ddb4-11ea-8851-c95e9b3edb78.JPG)
###### 1.1 Android Code
###### - 앱을 설치한 이후, 구글 로그인에 성공하였다면 그 이후부터는 자동으로 로그인이 진행됩니다.
###### - 이전 로그인 여부를 bool 형식으로 sharedPreferences에 저장하고 그 데이터(isLogin)를 앱 실행 시 검사하여, true라면 자동으로 다음 Activity를 실행합니다.
```java
private sharedPreferences pref;
private SharedPreferences.Editor editor;

if(pref.getBoolean("isLogin",false)){
  HashMap<String,String> body = new HashMap<>();
  body.put("email",pref.getString("userEmail",""));
  requestLogin(body);
}
```
###### - 사용자가 로그인 Button을 클리하면 구글 로그인 API를 수행하여 그 결과값을 받아옵니다.
```java
firebaseAuth = FirebaseAuth.getInstance();
btnGoogleLogin = findViewById(R.id.btn_login);

//google login
GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
googleSignInClient = GoogleSignIn.getClient(this. googleSignInOption);
btnGoogleLogin.setOnClickListener( (v) => {
  Log.i(TAG, "Login Click");
  Intent signInIntent = googleSignInClient.getSignInIntent();
  startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
});
```
```java
//google Login button response
if(requestCode == SIGN_IN_REQUEST_CODE){
  Task<GoogleSignInAccount> task = GoogleSignIn.geSignedInAccountFromIntent(data);
  try{
    //google Login success
    GoogleSignInAccount account = task.getResult(ApiException.class);
    
    HashMap<String, String> body = new HashMap<>();
    body.put("email", account.getEmail());
    body.put("nickname", account.getGivenName());
    body.put("profile", account.getPhotoUrl().toString();
    
    requestLogin(body);
  }catch(ApiException exception){
    Log.e(TAG, exception.toString();
  }
}
```
###### - 구글 로그인이 성공했다면, 필요한 사용자 정보를 Hashmap에 저장한다.
###### - 이 때, 구글 로그인 사용자 정보는 GoogleSignInAccount 객체가 가지고 있다.
###### Ex. (GoogleSignInAccount).getEmail( ) : 사용자의 구글 이메일 주소
###### Ex. (GoogleSignInAccount).getPhotoUrl( ) : 사용자의 구글 프로필 사진 주소(Url 객체이므로 String으로 ###### 저장할 때는 toString( ) 함수를 통해 String으로 변경해줄 필요가 있다.)
###### - 로그인에 성공했을 시, 서버로 로그인 요청을 보낸다.(requestLogin(body))
###### 이 때, 첫 로그인 요청은 email, nickname, profile 정보를 자동 로그인 시에는 email 정보만을 보내준다.
