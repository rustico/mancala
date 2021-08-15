<template>
  <div class="game">
    <MancalaBoard
      :playerBoard="playerBoard"
      :playerBank="playerBank"
      :oppositeBoard="oppositeBoard"
      :oppositeBank="oppositeBank"
    ></MancalaBoard>

    <div v-if='isPlayer && !invitationLink'>
      <div class="choosePitText">Choose a pit to move stones</div>
      <div class="choosePit"> 
          <button :disabled="!isPlayerTurn" id="send" class="choosePitButton" type="button" @click="send(1)" v-bind:class="{ buttonDisabled: !isPlayerTurn}">1</button>
          <button :disabled="!isPlayerTurn" id="send" class="choosePitButton" type="button" @click="send(2)" v-bind:class="{ buttonDisabled: !isPlayerTurn}">2</button>
          <button :disabled="!isPlayerTurn" id="send" class="choosePitButton" type="button" @click="send(3)" v-bind:class="{ buttonDisabled: !isPlayerTurn}">3</button>
          <button :disabled="!isPlayerTurn" id="send" class="choosePitButton" type="button" @click="send(4)" v-bind:class="{ buttonDisabled: !isPlayerTurn}">4</button>
          <button :disabled="!isPlayerTurn" id="send" class="choosePitButton" type="button" @click="send(5)" v-bind:class="{ buttonDisabled: !isPlayerTurn}">5</button>
          <button :disabled="!isPlayerTurn" id="send" class="choosePitButton" type="button" @click="send(6)" v-bind:class="{ buttonDisabled: !isPlayerTurn}">6</button>

      </div>
    </div>

    <div class="notYourTurn" v-if="isPlayer && !isPlayerTurn">Not your turn</div>

    <div class="invitation" v-if="invitationLink">
      <span class="inviteLinkText"> Please invite a friend to start playing</span>
      <div>
        <span class="inviteLinkText"> Invite Link:</span>
        <input type="text" :value="invitationLink" class="invitationLink">
        <button @click="copyLink()" class="copyButton">Copy</button> 
      </div>
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
    copyLink() {
      navigator.clipboard.writeText(this.invitationLink);
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

<style>
.invitation {
  margin-top: 50px;
}

.invitationLink {
  margin-top: 5px;
  padding: 6px 6px;
  box-sizing: border-box;
  width: 25%;
}

.copyButton {
  /* background-image: url('~@/assets/copy.svg'); */
  font-size: 13px;
  padding: 5px;
}

.inviteLinkText {
  font-weight: bold;
}

.choosePitButton {
    height: 50px;
    width: 50px;
    font-size: 25px;
    margin: 5px;
    background-color: rgb(121 56 56);
    border: none;
}

.choosePitButton:hover {
  cursor: pointer;
}

.buttonDisabled:hover {
  cursor: not-allowed;
}

.choosePitText {
  margin-top: 20px;
  font-weight: bold;
}

.notYourTurn {
  margin-top: 10px;
  font-weight: bold;
}
</style>
