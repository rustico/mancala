<script>
import config from '../config'
import axios from "axios";

export default {
  name: 'NewGame',
  created() {
    localStorage.clear()
    axios.post(`${config.API_URL}/games/new`, {numberOfStones: 6})
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
