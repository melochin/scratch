import {FocusButton} from '../anime/focus/button';

$(document).ready(function() {

    /**
     * <div class="anime-focus" focus="true/false" anime="animeId">
     */
    $(".anime-focus").map((index) => {
        var element = $(".anime-focus").get(index);
        var focus = $(element).attr("focus");
        focus = focus == "true";
        var animeId = $(element).attr("anime");
        ReactDOM.render(<FocusButton focus={focus} animeId={animeId} />, element);
    })
});