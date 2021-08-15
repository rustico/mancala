<template>
    <div class="board">
        <div class="section endsection">
            <MancalaPit :totalPits="oppositeBank" height="120"></MancalaPit>
        </div>
        <div class="section midsection">
        <div class="midrow topmid">
            <MancalaPit :totalPits="oppositeBoard[0]" height="60"></MancalaPit>
            <MancalaPit :totalPits="oppositeBoard[1]" height="60"></MancalaPit>
            <MancalaPit :totalPits="oppositeBoard[2]" height="60"></MancalaPit>
            <MancalaPit :totalPits="oppositeBoard[3]" height="60"></MancalaPit>
            <MancalaPit :totalPits="oppositeBoard[4]" height="60"></MancalaPit>
            <MancalaPit :totalPits="oppositeBoard[5]" height="60"></MancalaPit>
        </div>
        <div class="midrow">
            <MancalaPit :totalPits="playerBoard[0]" height="60"></MancalaPit>
            <MancalaPit :totalPits="playerBoard[1]" height="60"></MancalaPit>
            <MancalaPit :totalPits="playerBoard[2]" height="60"></MancalaPit>
            <MancalaPit :totalPits="playerBoard[3]" height="60"></MancalaPit>
            <MancalaPit :totalPits="playerBoard[4]" height="60"></MancalaPit>
            <MancalaPit :totalPits="playerBoard[5]" height="60"></MancalaPit>
        </div>
        </div>
        <div class="section endsection">
            <MancalaPit :totalPits="playerBank" height="120"></MancalaPit>
        </div>
    </div>
</template>

<script>
import MancalaPit from '@/components/MancalaPit.vue'
export default {
  name: 'MancalaBoard',
  components: {
    MancalaPit
  },
  props: {
    playerBoard: Array,
    playerBank: Number,
    oppositeBoard: Array,
    oppositeBank: Number,
  }
}
</script>

<style scoped>
.board
{
  display:flex;
  height:200px;
  width:800px;
  border-radius:50px;
  padding:30px;
  background-color: rgb(171, 98, 39);
  margin: auto;
}

.section
{
  margin:5px;
}

.endsection
{
  display:flex;
  flex-grow:1;
}

.midsection
{
  display:flex;
  flex-direction:column;
  flex-grow:6;
}

.midrow
{
  display:flex;
  flex-grow:1;
}

</style>
