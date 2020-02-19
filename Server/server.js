// //사용자 인증 로그인
// const passport = require('passport');
// const googleCredentials = require('./config/google.json');
// const GoogleStrategy = require('passport-google-oauth').OAuth2Strategy;
//
// console.log(googleCredentials.web.client_id);
//
// passport.use(new GoogleStrategy({
//     clientID : googleCredentials.web.client_id,
//     clientSecret : googleCredentials.web.client_secret,
//     callbackURL:googleCredentials.web.redirect_uris[0]
//   },
//   function(accessToken, refreshToken, profile, done){
//     console.log("accessToken",accessToken);
//     console.log("refreshToken",refreshToken);
//     console.log("profile",profile);
//   }
// ));

//database
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost:27017/atoz');

var db = mongoose.connection;
db.on('error',function(error){
  console.log('Database Connection Error : ',error);
});
db.on('open',function(){
  console.log('Database Connection Success');
});
//db schema
//post Database
var postSchema = new mongoose.Schema({
  user_id: {type:String, required: true}, //post를 작성한 user id
  media_type: {type:Number, required : true}, //0 : video, 1 : audio
  thumbnail : {type:String, required: true}, //thumbnail image name
  media : {type:String,required:true}, //media data name
  title : {type:String,default:""}, //post title
  description : {type:String,default:""}, //post description
  date : {type:Date, default:Date.now}, //upload time
  commentable : {type:Boolean, default:false},
  anonymous : {type:Boolean, default:false},
  messagable : {type:Boolean, default:false},
  downloadable : {type:Boolean, default:false},
  like_no : {type:Number, default : 0},
  comment_no : {type:Number, default: 0},
  comment: [{
    nickname: {type:String,required:true},
    profile: {type:String,required:true},
    description: {type:String,required:true}
  }]
});


//String, Number, Schema.Types.ObjectId, Date, Buffer, Boolean
//Schema.Types.Mixed(Object, JSONArray), Array(ex. [Number])
//required(NonNull), unique, trim, default, lowercase, match(정규식)
//validate(함수), ref(해당하는 모델을 참조할 때 사용)
//set(값을 입력할 때 함수 사용), get(값을 출력할 때 함수 사용)
var userSchema = new mongoose.Schema({
  email : {type:String, required:true},
  nickname : {type : String, required : true},
  // password : {type : String, required : true},
  profile : {type : String, default:"default_profile_image.jgp"},
  album : [{type:String}],
  media_id_like : [{type:String}],
  like_no : {type:Number,default:0},
  comment_no : {type:Number, default:0}
});

var Post = mongoose.model('Post',postSchema);
var User = mongoose.model('User',userSchema);

// var user = new User({
//   nickname : "Haman",
//   password : "12345"
// });
//
// user.save(function(error, post){
//   console.log("add user");
// });

//server
var express = require('express');
var app = express();
var multer = require('multer');
var upload = multer({dest : 'post_image'});
var bodyParser = require('body-parser');
var multipart=require('connect-multiparty');
var path = require('path');

// var router = require('./router/main')(app);
const PORT = 8080;

//server
app.listen(PORT, function(){
  console.log("Server Logging......")
});

app.use(bodyParser.json()); //json
app.use(express.static(__dirname+'/media'));
app.use('/static',express.static(__dirname+'/media'));
//Can Use req.file -> Store
// app.use(multipart({uploadDir:__dirname+'/media'}));
var storage = multer.diskStorage({
  destination:function(req,file,callback){
    callback(null,'media/')
  },
  filename:function(req,file,callback){
    var filename = file.originalname+"_"+Date.now().toString();
    var extension = path.extname(file.originalname);
    callback(null,filename+extension);
    // callback(null,file.originalname);
  }
});
var upload = multer({storage : storage});

//google login
// app.get('/auth/google',
//   passport.authenticate('google',{
//     scope:['https://www.googleapis.com/auth/plus.login']
// }));

//get post
app.get('/getPost',function(req,res){
  console.log("request All Post");
  Post.find(function(error, posts){
    if(error) res.send({responseCode:404,responseBody:"Fail"});
    else res.json({responseCode : 200, responseBody : posts});
  });
});

//get user post
//ex. Post.find({status:"C"},{_id:0, ename:1})
// == SELECT ename FROM Post Where status = 'C'
app.get('/getUserPost/:userEmail',function(req,res){
  console.log(req.params.userEmail);
  Post.find({user_id:req.params.userEmail},function(error,posts){
    if(error){
      console.log("Get User Post Data Base Error : "+error);
      res.send({responseCode:404,responseBody:[]});
    }else{
      res.send({responseCode:200,responseBody:posts});
    }
  });
});

//get user album media list
app.get('/getUserAlbum/:userEmail',function(req,res){
  console.log(req.params.userEmail);
  User.find({email:req.params.userEmail},{album:1},function(error,result){
    console.log(result[0].album);
    Post.find({_id:result[0].album},function(error,posts){
      if(error){
        console.log("Get User Post Data Base Error : "+error);
        res.send({responseCode:404,responseBody:[]});
      }else{
        res.send({responseCode:200,responseBody:posts});
      }
    });
  });
});

//add user album media list
app.put('/changeAlbumState',function(req,res){
  console.log(req.body.requestBody);
  var requestBody = JSON.parse(req.body.requestBody);
  User.find({email:requestBody.user_id,album:requestBody.media_id},function(error,result){
    console.log(result.length);
    if(error){ //에러 발생
      res.send({responseCode:404,responseBody:"Error"});
    }
    else if(result == null || result.length == 0){ //해당 media가 앨범에 포함되어 있지 않음.
      User.updateOne({email:requestBody.user_id,$push:{album:requestBody.media_id}},function(error){
        if(error){
          res.send({responseCode:404,responseBody:"Error"});
        }else{
          res.send({responseCode:200,responseBody:"Success Add Album"});
        }
      });
    }else{ //이미 해당 media가 앨범에 포함되어 있음. -> 앨범에서 제거
      User.updateOne({email:requestBody.user_id},{$pull:{album:requestBody.media_id}},function(error){
        if(error){
          res.send({responseCode:404,responseBody:"Error"});
        }else{
          res.send({responseCode:201,responseBody:"Success Remove Album"});
        }
      });
    }
  });
});

//upload post
app.post('/uploadPost',upload.single('file'),function(req,res,next){
  var requestBody = JSON.parse(req.body.description);
  console.log(requestBody);
  var post = new Post({
    user_id:requestBody.userEmail,
    media_type:requestBody.media_type,
    media:req.file.filename,
    title:requestBody.title,
    description:requestBody.description,
    commentable:requestBody.commentable,
    anonymous:requestBody.anonymous,
    messagable:requestBody.messagable,
    downloadable:requestBody.downloadable
  });

  post.save(function(error, post){
    if(error) {
      console.log("Database Error : "+error);
      res.send({responseCode:404, responseBody:"Fail"});
    }
    else res.send({responseCode:200,responseBody:"Ok"});
  });
});

//change post
app.post('/changePostData',function(req,res){

});
//signed up
app.post('/userSignedUp',function(req,res){

});
//change user data
app.post('/changeUserData',function(req,res){

});

//user request checking like
//1. 게시글은 그냥 좋아요 개수만 증가
//2. 좋아요 누른 게시글 정보는 user 정보에 포함
app.put('/changeLikeState',function(req,res){
  console.log(req.body.requestBody);
  var requestBody = JSON.parse(req.body.requestBody);
  User.find({email:requestBody.user_id,media_id_like:requestBody.media_id},function(error,result){
    console.log(result.length);
    if(error){ //에러 발생
      res.send({responseCode:404,responseBody:"Error"});
    }
    else if(result == null || result.length == 0){ //해당 media를 좋아요!
      User.updateOne({email:requestBody.user_id},{$push:{media_id_like:requestBody.media_id},$inc:{like_no:1}},function(error){
        if(error){
          res.send({responseCode:404,responseBody:"Error"});
        }else{
          Post.findOneAndUpdate({_id:requestBody.media_id},{$inc:{like_no:1}},function(error,posts){
            console.log(posts);
            if(error){
                res.send({responseCode:404,responseBody:"Error"});
            }else{
              var like = posts.like_no+1;
              console.log("kike_no : "+like);
              res.send({responseCode:200,responseBody:String(like)});
            }
          });
        }
      });
    }else{ //이미 해당 media에 좋아요 표시함 -> 좋아요 취소
      User.updateOne({email:requestBody.user_id},{$pull:{media_id_like:requestBody.media_id},$inc:{like_no:-1}},function(error){
        if(error){
          res.send({responseCode:404,responseBody:"Error"});
        }else{
          Post.findOneAndUpdate({_id:requestBody.media_id},{$inc:{like_no:-1}},function(error,posts){
            console.log(posts);
            if(error){
              res.send({responseCode:404,responseCode:"Error"});
            }else{
              var like = posts.like_no-1;
              res.send({responseCode:201,responseBody:String(like)});
            }
          });
        }
      });
    }
  });
});

app.get('/checkLike/:userEmail/:mediaId',function(req,res){
  console.log(req.params.userEmail+ " / "+req.params.mediaId);
  User.find({email:req.params.userEmail,media_id_like:req.params.mediaId},function(error,result){
    console.log(result);
    if(error){
      res.send({responseCode:404,responseBody:"Error"});
    }else if(result.length != 0){ //좋아요 표시함
      res.send({responseCode:200,responseBody:"Contains"});
    }
  });
});

//get user data
app.get('/userData/:userEmail',function(req,res){
  console.log("getUserData : "+req.params.userEmail);
  User.find({email:req.params.userEmail},{profile:1,nickname:1},function(error,result){
    console.log(result.length);
    if(error){
      res.send({responseCode : 404, responseBody : "Error"});
    }else if(result.length != 0){
      res.send({responseCode:200,responseBody:result[0]});
    }
  });
});

//회원 가입
app.put('/signIn/google',function(req,res){
  // console.log(req);
  // {email : , nickname : , profile : }
  User.find({email : req.body.email},function(error,result){
    if(error){
      res.send({responseCode:404,responseBody:"Error"});
    }else if(result.length == 0){ //회원 등록
      var user = new User({
        email : req.body.email,
        nickname : req.body.nickname,
        profile : req.body.profile
      });

      user.save(function(error, post){
        console.log(post);
        if(error) {
          console.log("Database Error : "+error);
          res.send({responseCode:404, responseBody:"Fail"});
        }
        else res.send({responseCode:200,responseBody:post});
      });
    }else{
      console.log(result[0]);
      res.send({responseCode:200,responseBody:result[0]});
    }
  });
});
