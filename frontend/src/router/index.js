import { createRouter, createWebHistory } from 'vue-router'
import NewGame from '../views/NewGame.vue'

const routes = [
  {
    path: '/',
    name: 'NewGame',
    component: NewGame
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  },
  {
    path: '/:uuid',
    name: 'Game',
    component: () => import(/* webpackChunkName: "game" */ '../views/Game.vue')
  },
  {
    path: '/:uuid/join/:apiKey',
    name: 'Invitation',
    component: () => import(/* webpackChunkName: "invitation" */ '../views/Invitation.vue')
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
