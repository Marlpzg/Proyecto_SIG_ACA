const user = {
    _id: "",
    username: "",
    passwd: "",
    name: "",
    lastName: "",
    email: "",
    gender: ""
}

const event = {
    _id: "",
    type: "",
    desc: "",
    coords: [lat, lon], 
    votes: [{user_id,score}] //Like +1, Dislike -1
}