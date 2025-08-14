import axios from "axios";
const API_BASE_URL = process.env.VUE_APP_API_URL;

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 20000,
})
// const api = axios.create({
//     baseURL: "http://localhost:8080/"
// })

function ListProfile(){
    return api.get("/profile");
}
function UpdateProfile(data : any){
    return api.put("/profile", data);
}



export default{
  ListProfile,
  UpdateProfile};