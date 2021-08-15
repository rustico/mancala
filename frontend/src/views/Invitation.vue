<script>
import config from '../config'
import axios from "axios";
export default {
  name: 'Invitation',
  created() {
    localStorage.clear()
    let gameUuid = this.$route.params.uuid;
    let apiKey = this.$route.params.apiKey;
    axios.get(`${config.API_URL}/games/${gameUuid}/join/${apiKey}`)
    .then((result) => {
      console.log(result) 
      localStorage.apiKey = result.data.apiKey
      localStorage.gameUuid = result.data.uuid

      this.$router.push({ 
        name: 'Game' ,
        params: { uuid: result.data.uuid }
      })
    });
  },
}
</script>
