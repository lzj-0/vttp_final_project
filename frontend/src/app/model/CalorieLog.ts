export interface CalorieLog {
    _id : { $oid : string },
    name : string,
    meal : string,
    calories : number,
    date : number,
    userId : string
}