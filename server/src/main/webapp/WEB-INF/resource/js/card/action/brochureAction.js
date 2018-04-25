function save(brochure, callback) {
    Ajax.post("/api/brochures", brochure, {
        success : function () {
            callback();
        }
    });
}

function list(callback) {
    Ajax.get("/api/brochures", null, {
        success : function (brochures) {
            var options = brochures.map(brochure => {
                var option = new Object();
                option.text = brochure.name;
                option.value = brochure;
                return option;
            });
            callback({brochures : brochures, options : options});
        }
    });
}

function remove(brochure, callback) {
    Ajax.delete("/api/brochures/" + brochure.id, null, {
        success : function() {
            callback();
        }
    });
}

module.exports.save = save;
module.exports.list = list;
module.exports.remove = remove;
