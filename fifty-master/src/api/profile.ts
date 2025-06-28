import axios from "axios";

const instance = axios.create({
    baseURL: "http://localhost:8080/"
})

function ListProfile(){
    return instance.get("/profile");
}
function UpdateProfile(data : any){
    return instance.put("/profile", data);
}



export default{
  ListProfile,
  UpdateProfile};