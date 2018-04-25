function list(brochureId, callback) {
    Ajax.get("/api/brochures/" + brochureId + "/cards", null, {
        success : function (data) {
            callback(data);
        }
    });
}

/**
 * @param brochureId
 * @param callback function(data)
 */
function listRemeber(brochureId, callback) {
    Ajax.get("/api/brochures/" + brochureId + "/cards/memory", null, {
        success : function (data) {
            callback(data)
        }
    });
}

function save(brochureId, card, callback) {
    Ajax.post("/api/brochures/" + brochureId + "/cards", card, {
        success : function () {
            callback();
        }
    });
}

function saveList(brochureId, cards, callback) {
    Ajax.post("/api/brochures/" + brochureId + "/cards/list", cards, {
        success : function () {
            callback();
        }
    });
}

function remove(brochureId, card, callback) {
    Ajax.delete("/api/brochures/" + brochureId + "/cards", card, {
        success : function () {
            callback();
        }
    });
}

function swap(brochureId, firstId, secondId, callback) {
    Ajax.put("/api/brochures/" + brochureId + "/cards/swap/" + firstId + "/" + secondId , null, {
        success : function () {
            callback();
        }
    });
}


module.exports.list = list;
module.exports.listRemeber = listRemeber;
module.exports.save = save;
module.exports.saveList = saveList;
module.exports.remove = remove;
module.exports.swap = swap;
