import { ILineChartResponse } from '../models';
import colors from 'analytics/src/utils/colors';

export function convertLineChartReponseToDoughnut(response: ILineChartResponse, labelKey = '') {
  const labels = response.definition[labelKey];
  const total = response.lines[0].dataset.reduce((acc, current) => acc + current, 0);

  return {
    type: 'doughnut',
    data: {
      labels,
      datasets: [
        {
          label: response.lines[0].name,
          backgroundColor: colors,
          data: response.lines[0].dataset,
          borderWidth: 1
        }
      ]
    },
    options: {
      elements: {
        center: {
          text: total,
          fontSizeFactor: 0.8,
          // sidePadding: 20, // Defualt is 20 (as a percentage)
          // color: '#000', // Default is #000000
          // fontStyle: 'Arial', // Default is Arial
          yShift: 10
          // xShift: 10
        }
      }
    }
  };
}
