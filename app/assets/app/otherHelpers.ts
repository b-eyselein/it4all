export function domReady(fn: () => void): void {
    if (document.readyState === 'loading') {
        document.addEventListener("DOMContentLoaded", fn);
    } else {
        fn();
    }
}

export function escapeHtml(unescapedHtml: string): string {
    return unescapedHtml
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

export function unescapeHTML(escapedHTML: string): string {
    return escapedHTML
        .replace(/&amp;/g, '&')
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&quot;/g, "\"")
        .replace(/&#039;/g, "'");
}

// Sample solutions

export interface SampleSolution<SampleType> {
    id: number;
    sample: SampleType;
}

export interface StringSampleSolution extends SampleSolution<string> {
}

export function displayStringSampleSolution(s: StringSampleSolution): string {
    return `
<div class="card my-3">
    <div class="card-body bg-light">
        <pre>${s.sample.trim()}</pre>
    </div>
</div>`.trim();
}

export function initShowSampleSolBtn<T>(renderSampleSolResponse: (T) => string): void {
    const showSampleSolBtn = document.querySelector<HTMLButtonElement>('#showSampleSolBtn');

    showSampleSolBtn.onclick = () => {
        showSampleSolBtn.disabled = true;
        fetch(showSampleSolBtn.dataset['url'])
            .then(response => {
                if (response.status === 200) {
                    response.json().then(response => {
                        document.querySelector<HTMLDivElement>('#sampleSolDiv').innerHTML = renderSampleSolResponse(response);
                    });
                    showSampleSolBtn.remove();
                } else {
                    response.text().then(error => console.error(error));
                }
            })
            .catch(reason => console.error(reason))
    }
}
