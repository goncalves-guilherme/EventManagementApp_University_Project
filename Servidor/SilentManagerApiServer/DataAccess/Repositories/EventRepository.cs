using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;
using SilentManagerApiServer.Models.DB;
using SilentManager.API.Models.DB;

namespace SilentManagerApiServer.DataAccess
{
    public class EventRepository : Repository<Event>, IEventRepository
    {
        private readonly SilentManagerContext dbContext;

        public EventRepository(SilentManagerContext dbContext)
            : base(dbContext)
        {
            this.dbContext = dbContext;
        }

        public IQueryable<Location> getLocationId(int id)
        {
            return from Location l in this.dbContext.Location
                   where l.LocationId == id
                   select new Location
                   {
                       LocationId = l.LocationId
                   };
        }


        public Task<IQueryable<CompletedEvent>> GetEventsByDistanceRangeAsync(double latitute, double longitude, int radius, string category, DateTime? date)
        {

            return Task.Run(() =>
                    from location in this.dbContext.Location
                    join _event in this.dbContext.Event
                    on location.LocationId equals _event.Location
                    join _user in this.dbContext.User
                    on _event.Author equals _user.UserId
                    join ev_cate in this.dbContext.EventHasCategories
                    on _event.EventId equals ev_cate.EventId
                    join cate in this.dbContext.Category
                    on ev_cate.CategoryId equals cate.CategoryId
                    where GetDistance(location.Latitude.Value, location.Longitude.Value, latitute, longitude) <= radius
                    && String.IsNullOrEmpty(category) ? true : cate.Category1.Equals(category)
                    //&& (date == null || _event.EndDate > date)
                    select new CompletedEvent
                    {
                        Author = new CompletedUser
                        {
                            UserId = _user.UserId,
                            Name = _user.Name,
                            Email = _user.Email,
                            CreateDate = _user.CreateDate
                        },
                        Location = new CompletedLocation
                        {
                            LocationId = location.LocationId,
                            Latitude = location.Latitude,
                            Longitude = location.Longitude,
                            Address = location.Address
                        },
                        EndDate = _event.EndDate,
                        StartDate = _event.StartDate,
                        EventId = _event.EventId,
                        State = _event.State,
                        Name = _event.Name,
                        Description = _event.Description,
                        Category = cate.Category1,
                        Radius = _event.Radius
                    });

        }

        public Task<IQueryable<CompletedEvent>> GetEventsByUserAsync(int userId)
        {
            return Task.Run(() =>
            {
                return from location in this.dbContext.Location
                       join _event in this.dbContext.Event
                       on location.LocationId equals _event.Location
                       join _user in this.dbContext.User
                       on _event.Author equals _user.UserId
                       join ev_cate in this.dbContext.EventHasCategories
                       on _event.EventId equals ev_cate.EventId
                       join _cate in this.dbContext.Category
                       on ev_cate.CategoryId equals _cate.CategoryId
                       where (_user.UserId == userId)
                       select new CompletedEvent
                       {
                           Author = new CompletedUser
                           {
                               UserId = _user.UserId,
                               Name = _user.Name,
                               Email = _user.Email,
                               CreateDate = _user.CreateDate
                           },
                           Location = new CompletedLocation
                           {
                               LocationId = location.LocationId,
                               Latitude = location.Latitude,
                               Longitude = location.Longitude,
                               Address = location.Address
                           },
                           EndDate = _event.EndDate,
                           StartDate = _event.StartDate,
                           EventId = _event.EventId,
                           State = _event.State,
                           Name = _event.Name,
                           Radius = _event.Radius,
                           Description = _event.Description,
                           Category = _cate.Category1
                       };
            });
        }

        private double GetDistance(double lat1, double lon1, double lat2, double lon2)
        {
            var R = 6371; // Radius of the earth in km
            var dLat = ToRadians(lat2 - lat1);  // deg2rad below
            var dLon = ToRadians(lon2 - lon1);
            var a =
                Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                Math.Cos(ToRadians(lat1)) * Math.Cos(ToRadians(lat2)) *
                Math.Sin(dLon / 2) * Math.Sin(dLon / 2);

            var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            var d = R * c; // Distance in km
            return d * 1000;
        }

        double ToRadians(double deg)
        {
            return deg * (Math.PI / 180);
        }

    }
}
