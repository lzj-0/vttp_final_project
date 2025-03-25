import { Review } from "./Review";

export interface RecipeDetail {
    _id : { $oid : string },
    title : string,
    ingredients: string[],
    preparationTime : number,
    summary : string,
    servingSize : number,
    imageUrl : string,
    instructions : string[],
    cuisine : string,
    meal : string,
    calories : number,
    averageRating : number,
    reviews : number;
    views : number,
    isGateKept : boolean,
    isPrivate : boolean,
    email : string,
    ownerTier : string,
    reviewsList : Review[],
    createdAt : number
}