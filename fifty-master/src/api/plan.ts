import axios from "axios";
const API_BASE_URL = process.env.VUE_APP_API_URL;

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 20000,
})
// const api = axios.create({
//     baseURL: "http://localhost:8080/"
// })


function backgroundList(month: number) {
  return api.get("/months", { params: { month } });
}

function updateBackground(data : any){
    return api.put("/months", data);
}

function getInsertPlan(data : any){
    return api.post("/plan", data)
} 

function getUpdatePlan(data:any) {
    return api.put("/plan", data)
}
function getDeletePlan(no:number) {
    return api.delete(`/plan/${no}`)  
}

function ListPlan(){
  return api.get("/plan");
}

function ListCalendar(){
  return api.get("/months/list");
}

export default{
  updateBackground,
  backgroundList,
  getInsertPlan,
  getUpdatePlan,
  getDeletePlan,
  ListPlan,
  ListCalendar
};