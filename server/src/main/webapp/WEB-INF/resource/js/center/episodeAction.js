
function page(pageNo, callback) {
    Ajax.get("/api/user/episodes/page/" + pageNo, null, {
        success : function (data) {
            callback(data);
        }
    })
}

function pageFrom(pageStart, pageEnd, callback) {
    Ajax.get("/api/user/episodes/page/" + pageStart + "/" + pageEnd, null, {
        success : function (data) {
            callback(data);
        }
    })
}


export {page, pageFrom}
