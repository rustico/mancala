<template>
  <div class="game">
      <div v-if="invitationLink"> {{ invitationLink }} </div>
        <button id="send" class="btn btn-default" type="submit" @click.prevent="send">Send</button>

        <div> {{oppositeBank}} {{oppositeBoard}} </div>
        <div> {{playerBoard}} {{playerBank}} </div>

        <div> 
            <button id="send" class="btn btn-default" type="button" @click="send(1)">1</button>
            <button id="send" class="btn btn-default" type="button" @click="send(2)">2</button>
            <button id="send" class="btn btn-default" type="button" @click="send(3)">3</button>
            <button id="send" class="btn btn-default" type="button" @click="send(4)">4</button>
            <button id="send" class="btn btn-default" type="button" @click="send(5)">5</button>
            <button id="send" class="btn btn-default" type="button" @click="send(6)">6</button>
        </div>
  </div>
</template>

<script>
import config from '../config'
import axios from "axios";
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";


export default {
  name: 'Game',
  data () {
    return {
      invitationLink: null,
      playerBoard: [],
      playerBank: 0,
      oppositeBoard: [],
      oppositeBank: 0,
    }
  },
  created() {
    localStorage.gameUuid = this.$route.params.uuid;
    axios.get(`${config.API_URL}/games/${localStorage.gameUuid}`)
    .then((result) => {
        if(localStorage.invitationApiKey) {
            this.invitationLink = `${window.location.origin}/${localStorage.gameUuid}/join/${localStorage.invitationApiKey}`;
        }

        this.playerBoard = result.data.playerOneBoard;
        this.playerBank = result.data.playerOneBank;
        this.oppositeBoard = result.data.playerTwoBoard;
        this.oppositeBank = result.data.playerTwoBank;
    });
  },
  methods: {
    send(position) {
        let data = {
            position: position,
            playerApiKey: localStorage.apiKey,
            gameUuid: localStorage.gameUuid
        }
        axios.post(`${config.API_URL}/gamesmoves`, data);
    },
    connect() {
      this.socket = new SockJS("http://localhost:8081/stomp");
      this.stompClient = Stomp.over(this.socket);
      this.stompClient.connect({}, () => {
          this.stompClient.subscribe(`/topic/${localStorage.gameUuid}`, payload => {
            let body = JSON.parse(payload.body)
            this.playerBoard = body.playerOneBoard;
            this.playerBank = body.playerOneBank;
            this.oppositeBoard = body.playerTwoBoard;
            this.oppositeBank = body.playerTwoBank;
          });

          if(localStorage.invitationApiKey) {
            this.stompClient.subscribe(`/topic/${localStorage.invitationApiKey}`, () => {
                localStorage.removeItem('invitationApiKey');
                this.invitationLink = null;
            });
          }
        },
      );
    }
  },
  mounted() {
    this.connect();
  }
}
</script>
