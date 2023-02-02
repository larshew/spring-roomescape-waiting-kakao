package roomescape.nextstep.reservation;

import roomescape.auth.AuthenticationException;
import roomescape.nextstep.member.Member;
import roomescape.nextstep.member.MemberDao;
import roomescape.nextstep.schedule.Schedule;
import roomescape.nextstep.schedule.ScheduleDao;
import roomescape.nextstep.support.DuplicateEntityException;
import roomescape.nextstep.theme.Theme;
import roomescape.nextstep.theme.ThemeDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    public final ReservationDao reservationDao;
    public final ThemeDao themeDao;
    public final ScheduleDao scheduleDao;
    public final MemberDao memberDao;

    public ReservationService(ReservationDao reservationDao, ThemeDao themeDao, ScheduleDao scheduleDao, MemberDao memberDao) {
        this.reservationDao = reservationDao;
        this.themeDao = themeDao;
        this.scheduleDao = scheduleDao;
        this.memberDao = memberDao;
    }

    public Long create(Member member, ReservationRequest reservationRequest) {
        if (member == null) {
            throw new AuthenticationException();
        }
        Schedule schedule = scheduleDao.findById(reservationRequest.getScheduleId());
        if (schedule == null) {
            throw new NullPointerException();
        }

        List<Reservation> reservation = reservationDao.findByScheduleId(schedule.getId());
        if (!reservation.isEmpty()) {
            throw new DuplicateEntityException();
        }

        Reservation newReservation = new Reservation(
                schedule,
                member
        );

        return reservationDao.save(newReservation);
    }

    public boolean isReserved(Long scheduleId) {
        List<Reservation> reservation = reservationDao.findByScheduleId(scheduleId);
        return !reservation.isEmpty();
    }

    public List<Reservation> findAllByThemeIdAndDate(Long themeId, String date) {
        Theme theme = themeDao.findById(themeId);
        if (theme == null) {
            throw new NullPointerException();
        }

        return reservationDao.findAllByThemeIdAndDate(themeId, date);
    }

    public void deleteById(Member member, Long id) {
        Reservation reservation = reservationDao.findById(id);
        if (reservation == null) {
            throw new NullPointerException();
        }

        if (!reservation.sameMember(member)) {
            throw new AuthenticationException();
        }

        reservationDao.deleteById(id);
    }

    public List<ReservationResponse> findAllByMemberId(Member member) {
        return reservationDao.findAllByMemberId(member.getId()).stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());
    }
}
