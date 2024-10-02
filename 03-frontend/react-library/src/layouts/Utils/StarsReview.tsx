import React from 'react';
import {Star} from "./Star";

export const StarsReview: React.FC<{ rating: number, size: number }> = ({rating, size}) => {

    let stars = rating;
    if (stars === undefined || stars < 0 || stars > 5) {
        stars = 0;
    }

    const fullStars = stars | 0;
    const halfStars = stars - fullStars >= 0.4 ? 1 : 0;
    const emptyStars = 5 - (fullStars + halfStars);

    return (
        <div>
            {Array.from({length: fullStars}, (_, i) =>
                <Star key={i} type='full' size={size}/>
            )}
            {Array.from({length: halfStars}, (_, i) =>
                <Star key={i} type='half' size={size}/>
            )}
            {Array.from({length: emptyStars}, (_, i) =>
                <Star key={i} type='empty' size={size}/>
            )}
        </div>
    );
};
