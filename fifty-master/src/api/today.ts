import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://localhost:8080',
})


 function checkToday() {
    return instance.post('/visit');
  }



  function today(){
      return instance.get("/visit/today");
  }
  function totalStats(){
      return instance.get("/visit/summary");
  }


  function stats(start : string, end: string){
      return instance.get("/visit/stats", {params: { start, end }});
  }

export default{
 today,
 totalStats,
 stats,
 checkToday
};