

        var mb_nick = $('#mb_nick').val();
	    var user_type = $('#user_type').val();
		var stompClient = null;
		var mb_nick_a = $('#mb_nick_a').val();
		var mb_name_a = $('#mb_name_a').val();
		var mb_name_b = $('#mb_name_b').val();
		var id = $('#id').val();
		var mb_nick_b = $('#mb_nick_b').val();
		var senderName = $('#mb_name').val();
		var senderId = $('#mb_no').val();

        $( document ).ready(function() {
                scroll_go();
            });

        $(document).ready(connect());
        function connect() {

			var socket = new SockJS('/broadcast');
			var url = '/topic/' + id + '/queue/messages';

				stompClient = Stomp.over(socket);
				stompClient.connect({}, function() {
				stompClient.subscribe(url, function(output) {
					showBroadcastMessage(createTextNode(JSON.parse(output.body)));
					});
				},
				function (err) {
					alert('서버와의 연결이 끊겼습니다. ' + err);
				});

		};
		$(document).ready(chatReadInterval());
        //$(document).ready(initialize());
initialize();
function initialize() {
			getChatList();
			getUnreadMessageInfo();
			unreadAlertInfinite();
		}

		//async(비동기)로 일정간격 업뎃되지 않아도 되는 img 파일을 불러옴
		function getChatList() {
			console.log("getChatList inprocess");
			$.ajax({
				url: "/chatList/ajax",
				type: "POST",
				data: JSON.stringify({
					nick: anick
				}),
				contentType: "application/json",
				//전달을 성공했을때 Controller로부터 data를 return 받아 처리해주는 메서드
				success: function(data) {

					 var parsed = JSON.parse(data);
					 var length = parsed.chatList.length;
					 for (var i = 0; i < length; i++) {
						 //채팅방 갯수만큼 반복문을 돌면서 채팅방 틀(div, img 태그)를 만들어줌
						 addChatDivImg(i);
					 }
				}
			});
		}

		 function getUnreadMessageInfo() {

				 $.ajax({
					 url:"/chatUnreadMessageInfo/ajax",
					 type: "POST",
					 data: JSON.stringify({
						 nick : anick
					 }),
					 contentType: "application/json",
					 success: function(data) {
						 var parsed = JSON.parse(data);
						 var length = parsed.chatList.length;

						 for (var i = 0; i < length; i++) {
							$('.wrapSellerTitle' + i).html('');
						 	addChatList(parsed.chatList[i].senderName, parsed.chatList[i].users_title, parsed.chatList[i].messageUnread, i);
						 }
					 }
			 });
		 }



	 	function unreadAlertInfinite() {
	 		setInterval(() => {
	 			getUnreadMessageInfo();
			}, 1000);
	 	}

        function scroll_go(){
        $('#content').scrollTop($('#content')[0].scrollHeight);
        }





		var inputMessage = document.getElementById('message');
		inputMessage.addEventListener('keyup', function enterSend(event) {

			if (event.keyCode === null) {
				event.preventDefault();
			}

			if (event.keyCode === 13) {
				send();
			}
		});


//일정 간격으로 업데이트된 데이터를 화면에 출력하는 메서드 됨
	 	function addChatList(senderName, users_title, messageUnread, idx) {

         var url = "/chatMessage.do?nick2="+ senderName;
	 		var str =
	 		"<a href='"+ url + "'"    + '>' +
	 		'<h3><div id="senderName">' +
	 		senderName +
	 		'&nbsp;</div>' +
	 		'<div id="message">' +
	 		messageUnread+'</div></h3></div></a>';

	 		//HTML화면의 <div class="wrapSellerTitle0,1,...etc"> 하위에 str 변수를 추가해준다.
	 		 $('.wrapSellerTitle' + idx).append(str);
	 	}

	 	//페이지가 로드되는 시점 한 번만 출력하면 되는 div, img를 출력하는 메서드
	 	function addChatDivImg(idx) {
	 			$('#wrapper').append('<div class= chatMessageInfo' + idx + '><div class="wrapSellerTitle' +
	 					idx +
	 					'"></div></div>');
	 	}

		function createTextNode(messageObj) {

			if($('#mb_nick').val() == $('#mb_nick_b').val()){
            var chatRead = $('#chatRoomRead.chatRead_a').val() == 1 ? '읽음': '';
            }

            if($('#mb_nick').val() == $('#mb_nick_a').val()){
            var chatRead = $('#chatRoomRead.chatRead_b').val() == 1 ? '읽음': '';
            }

			let mb_name = $("#mb_name").val();

			var origin_time = messageObj.sendTime;
			var time = origin_time.substring(0,5);

				if (mb_name == messageObj.senderName) {
                    return '<div class="chatbox_messages"><div class="chatbox_messageBox" style="text-align: right" class="chatRoomSenderName">'+
                    '<div class="chatbox_message chatRoomContent" style="margin-inline-start: auto;">' +
                    messageObj.content+
                    '</div><div class="chatRoomSendTime">' +
                    '<span>' +
                    chatRead +
                    '</span> ' +
                    time +
                    '</div></div></div>';
                }else {
				return '<div class="chatbox_messages"><div class="chatbox_messageBox" style="text-align: left" class="chatRoomSenderName"><div>' +
	            messageObj.senderName +
	            '</div><div class="img_box">' +
                '<div><img th:src="@{/upload/}+${target_mb_file}" class ="target_mb_file""></div>' +
                '</div><div class="chatbox_message chatRoomContent" style="margin-inline-end: auto;">' +
	            messageObj.content+
	            '</div><div class="chatRoomSendTime">' +
	            time +
	            '</div></div></div>';
			}
        }


		function showBroadcastMessage(message) {
            $("#content").html($("#content").html() + message);
            scroll_go();
        }

		function chatReadInterval() {
	 		setInterval(() => {
	 			ajaxChatRead(mb_nick);
			}, 1000);
	 	}

		function ajaxChatRead(reader) {

			var flag = "";
			if (reader == mb_nick_a) {
				flag = "mb_nick_a";
			} else {
				flag = "mb_nick_b";
			}

			$.ajax({
				url:'/chatread.do',
				type: 'POST',
				data: JSON.stringify({
					id: id,
					flag: flag
				}),
				dataType: 'json',
				contentType: 'application/json'
			})

		}
//--------------------------------------------------------------------------------


	      function sendBroadcast(json) {
			stompClient.send("/app/broadcast", {}, JSON.stringify(json));

		}


		function send() {

		var content_origin = $('#message').val();

			var content = content_origin.replace(/(<([^>]+)>)/ig,"");
			console.log(content);
			sendBroadcast({
				'id': id,
				'content': content,
				'mb_name_a': mb_name_a,
				'mb_name_b': mb_name_b,
				'mb_nick_a': mb_nick_a,
				'mb_nick_b': mb_nick_b,
				'senderName': senderName,
				'senderNick': mb_nick
				});
			$('#message').val("");
		}