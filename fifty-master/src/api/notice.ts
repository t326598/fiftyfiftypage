import axios from "axios";

const instance = axios.create({
    baseURL: "http://localhost:8080/"
})

function insertNotice(formData: any){
    return instance.post("/notice", formData);
}

function ListNotice() {
  return instance.get("/notice");
}

function deleteNotice(no: number) {
    return instance.delete(`/notice/${no}`);
}

function updateNotice( data:any) {
    return instance.put("/notice", data)
    
}


export default{
  insertNotice,
  ListNotice,
  deleteNotice,
  updateNotice
};