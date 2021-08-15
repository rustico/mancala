<template>
  <div class="home">
  </div>
</template>

<script>
import config from '../config'
import axios from "axios";
export default {
  name: 'Home',
  created() {
    localStorage.clear()
    axios.get(`${config.API_URL}/games/new`)
    .then((result) => {
      localStorage.apiKey = result.data.apiKey
      localStorage.invitationApiKey = result.data.invitationApiKey
      localStorage.gameUuid = result.data.uuid
      console.log(result.data);

      this.$router.push({ 
        name: 'Game' ,
        params: { uuid: result.data.uuid }
      })
    })
  },
}
</script>
