import * as $ from 'jquery';

export {toggleFullscreen};

declare global {
    interface Document {
        mozCancelFullScreen: any;
        mozFullScreenElement: any;

        msFullscreenElement: any;
        msExitFullscreen: () => void;
    }


    interface Element {
        mozRequestFullScreen?(): void;

        msRequestFullscreen?(): void;
    }
}


function toggleFullscreen(): void {
    const elem = document.getElementById('fullsc');

    if (!document.fullscreenElement && !document.mozFullScreenElement && !document.webkitFullscreenElement && !document.msFullscreenElement) {

        if (elem.requestFullscreen) {
            elem.requestFullscreen();
        } else if (elem.msRequestFullscreen) {
            elem.msRequestFullscreen();
        } else if (elem.mozRequestFullScreen) {
            elem.mozRequestFullScreen();
        } else if (elem.webkitRequestFullscreen) {
            elem.webkitRequestFullscreen();
        }

        $('#fullscBtnSpan').removeClass('glyphicon-resize-full').addClass('glyphicon-resize-small');

    } else {
        if (document.exitFullscreen) {
            document.exitFullscreen();
        } else if (document.msExitFullscreen) {
            document.msExitFullscreen();
        } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        } else if (document.webkitExitFullscreen) {
            document.webkitExitFullscreen();
        }

        $('#fullscBtnSpan').removeClass('glyphicon-resize-small').addClass('glyphicon-resize-full');
    }
}