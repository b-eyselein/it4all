import {domReady} from "../otherHelpers";

domReady(() => {
    const convButton: HTMLButtonElement = document.querySelector<HTMLButtonElement>('#convButton');
    convButton.onclick = () => {
        const fromBase: string = document.querySelector<HTMLSelectElement>('#convFromBase').value;
        const toBase: string = document.querySelector<HTMLSelectElement>('#convToBase').value;

        window.location.href = `${convButton.dataset['href']}?fromBase=${fromBase}&toBase=${toBase}`;
    };

    const addButton: HTMLButtonElement = document.querySelector<HTMLButtonElement>('#addButton');
    addButton.onclick = () => {
        const addBase = document.querySelector<HTMLSelectElement>('#addBase').value;

        window.location.href = `${addButton.dataset['href']}?base=${addBase}`;
    };

    const twoCompButton: HTMLButtonElement = document.querySelector<HTMLButtonElement>('#twoCompButton');
    twoCompButton.onclick = () => {
        const verbose: string = document.querySelector<HTMLSelectElement>('#twoComp').value;

        window.location.href = `${twoCompButton.dataset['href']}?verbose=${verbose}`;
    };
});
