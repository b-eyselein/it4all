interface CorrectionColors {
  correct: string;
  partlyCorrect: string;
  inCorrect: string;
}

export const bgColors: CorrectionColors = {
  correct: 'bg-green-500',
  partlyCorrect: 'bg-amber-400',
  inCorrect: 'bg-red-500',
};

export const textColors: CorrectionColors = {
  correct: 'text-green-500',
  partlyCorrect: 'text-amber-400',
  inCorrect: 'text-red-500',
};

export const linkColor = 'text-blue-700';
