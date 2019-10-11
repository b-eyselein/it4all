import {ExerciseFile} from "../tools/ideExercise";

export interface ProgSampleSolution {
    id: number;
    sample: {
        files: ExerciseFile[];
    }
}

export function showProgSampleSolutions(sampleSols: ProgSampleSolution[]): string {
    return sampleSols.map(sampleSolution =>
        sampleSolution.sample.files.map(file => `
<div class="card">
    <div class="card-header">${file.name}</div>
    <div class="card-body bg-light">
        <pre>${file.content}</pre>
    </div>
</div>`.trim()
        ).join('\n')
    ).join('\n');
}
