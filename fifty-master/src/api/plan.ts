import axios from "axios";

const instance = axios.create({
    baseURL: "http://localhost:8080/"
})



function backgroundList(month: number) {
  return instance.get("/months", { params: { month } });
}

function updateBackground(data : any){
    return instance.put("/months", data);
}

function getInsertPlan(data : any){
    return instance.post("/plan", data)
}

function getUpdatePlan(data:any) {
    return instance.put("/plan", data)
}
function getDeletePlan(no:number) {
    return instance.delete(`/plan/${no}`)  
}

function ListPlan(){
  return instance.get("/plan");
}

function ListCalendar(){
  return instance.get("/months/list");
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