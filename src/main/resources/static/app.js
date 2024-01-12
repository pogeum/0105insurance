var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
//    if (connected) {
//        $("#conversation").show();
//    }
//    else {
//        $("#conversation").hide();
//    }
//    $("#savemessages").html("");
}

function connect() {
    var socket = new SockJS('/aaa');
    stompClient = Stomp.over(socket);
    console.log(stompClient);

    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messaging', function (messaging) {
            showMessaging(JSON.parse(messaging.body));
        });
    });
}



function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendContent() {//이게  sendmessage!!!!!!!!!!
    // content 입력 필드에서 값 가져오기
    var contentValue = $("#content").val();

    // 템플릿의 id=receiver 에서 값 가져오기
    var receiverName = $("#receiver").val();

    // contentValue와 receiverName 포함한 메시지 객체 생성
        var message = {
            'content': contentValue,
            'receiver': receiverName
//            'sender' : sender
        };

//    stompClient.send("/app/hello", {}, JSON.stringify(message));

    // stompClient가 정의되어 있고 연결이 성공적인 경우에만 send 호출
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/hello", {}, JSON.stringify(message));
    } else {
        console.error('WebSocket connection is not established.');
    }

}

function showMessaging(message, myprofileName) { //이게  savemessage인듯

    let reg = /[`~!@#$%^&*()_|+\-=?;:'"<>\{\}\[\]\\\/ ]/gim;
    let myprofileNameReg = myprofileName.replace(reg, "");

    console.log(message.author);
    console.log(myprofileNameReg);
    console.log(typeof message.author);
    console.log(typeof myprofileName);



    if (message.author === myprofileNameReg) {
        // author가 현재 로그인한 사용자와 같은 경우, Me라는 id를 가진 tr에 출력
        console.log('same');
        $("#savemessages").append("<tr id='Me' class='pull-right'><td style='color: red; '>" + message.content + "</td></tr>");
    } else {
        // 그 외의 경우의 기본 출력 처리
        console.log('다름');
        $("#savemessages").append("<tr id='You' class='pull-left'><td style='color: blue; '>" + message.content + "</td></tr>");
    }

}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
//    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#btn-chat" ).click(function() { sendContent(); });
});