
OAuth.initialize('4tkYnk82EzCaOfpxiW73GTec2Gw');


function enterThroughFacebook() {


};

function enterThroughGitHub() {

    OAuth.popup('github').done(function (result) {

        //OAuth.initialize('4tkYnk82EzCaOfpxiW73GTec2Gw');
        //provider can be 'facebook', 'twitter', 'github', or any supported
//provider that contain the fields 'firstname' and 'lastname'
//or an equivalent (e.g. "FirstName" or "first-name")
        var provider = 'github';

        OAuth.popup(provider)
            .done(function (result) {
                result.me()
                    .done(function (response) {
                        /* console.log('Firstname: ', response.firstname);
                         console.log('Lastname: ', response.lastname);
                         console.log('response: ', response);*/

                        var xmlhttp = getXmlHttp();
                        xmlhttp.open("POST", "api/login", true);
                        xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                        xmlhttp.send("email=" + response.email);

                        /*if (xmlhttp.status == 200) {
                            alert(xmlhttp.responseText);
                        } else {
                            alert(xmlhttp.status)
                        }
                        ;*/

                    })
                    .fail(function (err) {
                        //handle error with err
                    });
            })
            .fail(function (err) {
                //handle error with err
            });
    })
};


function getXmlHttp(){
    var xmlhttp;
    try {
        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (E) {
            xmlhttp = false;
        }
    }
    if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
        xmlhttp = new XMLHttpRequest();
    }
    return xmlhttp;
};