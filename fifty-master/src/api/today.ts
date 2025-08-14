import axios from 'axios'
const API_BASE_URL = process.env.VUE_APP_API_URL;

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 20000,
})
// const api = axios.create({
//     baseURL: "http://localhost:8080/"
// })

 function checkToday() {
    return api.post('/visit');
  }



  function today(){
      return api.get("/visit/today");
  }
  function totalStats(){
      return api.get("/visit/summary");
  }


  function stats(start : string, end: string){
      return api.get("/visit/stats", {params: { start, end }});
  }

export default{
 today,
 totalStats,
 stats,
 checkToday
};