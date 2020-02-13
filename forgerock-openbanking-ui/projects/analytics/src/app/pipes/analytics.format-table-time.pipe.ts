import { Pipe, PipeTransform } from '@angular/core';
import format from 'date-fns/format';

@Pipe({
  name: 'analyticsTableTimeFormat',
  pure: true
})
export class AnalyticsTableTimeFormatPipe implements PipeTransform {
  transform(time: number): string {
    try {
      let label = '';
      const date = new Date(time);
      if (date.getMinutes() > 0) {
        label += format(date, 'm') + "<span class='response-time-unity'> m</span> ";
      }
      if (date.getSeconds() > 0) {
        label += format(date, 's') + "<span class='response-time-unity'> s</span> ";
      }
      if (date.getMilliseconds() > 0) {
        label += format(date, 'SSS') + "<span class='response-time-unity'> ms</span>";
      }
      return label;
    } catch (error) {
      return '';
    }
  }
}
