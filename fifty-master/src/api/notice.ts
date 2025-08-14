import axios from "axios";
const API_BASE_URL = process.env.VUE_APP_API_URL;

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 20000,
})
// const api = axios.create({
//     baseURL: "http://localhost:8080/"
// })

function insertNotice(formData: any){
    return api.post("/notice", formData);
}

function ListNotice() {
  return api.get("/notice");
}

function deleteNotice(no: number) {
    return api.delete(`/notice/${no}`);
}

function updateNotice( data:any) {
    return api.put("/notice", data)
    
}


export default{
  insertNotice,
  ListNotice,
  deleteNotice,
  updateNotice
};