<template>
  <div class="game">
      <div v-if="invitationLink"> {{ invitationLink }} </div>
        <div> {{oppositeBank}} {{oppositeBoard}} </div>
        <div> {{playerBoard}} {{playerBank}} </div>

        <MancalaBoard
          :playerBoard="playerBoard"
          :playerBank="playerBank"
          :oppositeBoard="oppositeBoard"
          :oppositeBank="oppositeBank"
        ></MancalaBoard>

        <div v-if='isPlayer'> 
            <button :disabled="!isPlayerTurn" id="send" class="btn btn-default" type="button" @click="send(1)">1</button>
            <button :disabled="!isPlayerTurn" id="send" class="btn btn-default" type="button" @click="send(2)">2</button>
            <button :disabled="!isPlayerTurn" id="send" class="btn btn-default" type="button" @click="send(3)">3</button>
            <button :disabled="!isPlayerTurn" id="send" class="btn btn-default" type="button" @click="send(4)">4</button>
            <button :disabled="!isPlayerTurn" id="send" class="btn btn-default" type="button" @click="send(5)">5</button>
            <button :disabled="!isPlayerTurn" id="send" class="btn btn-default" type="button" @click="send(6)">6</button>
        </div>
  </div>
</template>

<script>
import config from '../config'
import axios from "axios";
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";
import MancalaBoard from '@/components/MancalaBoard.vue'

export default {
  name: 'Game',
  components: {
    MancalaBoard
  },
  data () {
    return {
      invitationLink: null,
      playerBoard: [],
      playerBank: 0,
      oppositeBoard: [],
      oppositeBank: 0,
      isPlayerTurn: false,
      isPlayer: false,
    }
  },
  created() {
    localStorage.gameUuid = this.$route.params.uuid;
    this.isPlayer = !!localStorage.apiKey;

    axios.get(`${config.API_URL}/games/${localStorage.gameUuid}`)
    .then((result) => {
        if(localStorage.invitationApiKey) {
            this.invitationLink = `${window.location.origin}/${localStorage.gameUuid}/join/${localStorage.invitationApiKey}`;
        }
        this.updateState(result.data);
    });
  },
  methods: {
    updateState(data) {
        if(localStorage.apiKey && localStorage.apiKey.substring(0, 9) == data.playerTwoId) {
          this.playerBoard = data.playerTwoBoard;
          this.playerBank = data.playerTwoBank;
          this.oppositeBoard = data.playerOneBoard.reverse();
          this.oppositeBank = data.playerOneBank;
        } else {
          this.playerBoard = data.playerOneBoard;
          this.playerBank = data.playerOneBank;
          this.oppositeBoard = data.playerTwoBoard.reverse();
          this.oppositeBank = data.playerTwoBank;
        }

        this.isPlayerTurn = localStorage.apiKey.substring(0, 9) == data.playerTurn
    },
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
            this.updateState(body);
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