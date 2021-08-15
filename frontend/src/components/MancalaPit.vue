<template>
    <div class="pit">
        <span class="totalPits" v-bind:class="{ totalPitsTop: isTop }">{{totalPits}}</span>
        <span 
            v-for="n in totalPits" 
            class="stone"
            v-bind:style="{ 
                'background-color': getColor(),
                'top': Math.floor(Math.random() * height) + 'px',
                'left': Math.floor(Math.random() * 60) + 'px'
            }"></span>
    </div>
</template>

<script>
export default {
  name: 'MancalaPit',
  props: {
    totalPits: Number,
    height: Number,
    isTop: Boolean
  },
  methods: {
      getColor() {
        let colors = ['black', 'red', 'yellow', 'brown'];
        return colors[Math.floor(Math.random() * colors.length)];
      }
  }
}
</script>

<style scoped>
.pit
{
  position:relative;
  background-color: rgb(101, 52, 46);
  flex-grow:1;
  margin:5px;
  border-radius:50px;
}

.stone
{
  position:absolute;
  border-radius: 50%;
  width: 25px;
  height: 25px;
  background-color: red;
}

.totalPits {
    position: absolute;
    left: 45%;
    bottom: -26px;
    font-size: 23px;
}

.totalPitsTop {
    top: -26px;
}
</style>