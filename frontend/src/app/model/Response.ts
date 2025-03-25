export interface Response {
    status : number,
    message: string,
    token: string,
    userId : string,
    expActivity : {expAwarded : number, levelUp : boolean, tierUp : string}
}