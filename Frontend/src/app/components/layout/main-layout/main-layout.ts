import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../../sidebar/sidebar';
import { Topbar } from '../../topbar/topbar';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, Sidebar, Topbar],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './main-layout.css',
  templateUrl: './main-layout.html'
})
export class MainLayout {
  protected readonly menuAbierto = signal(false);
}
