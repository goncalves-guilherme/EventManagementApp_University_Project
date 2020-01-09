using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;
using AutoMapper;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.DataAccess;
using SilentManagerApiServer.Models.DB;
using SilentManager.API.BusinessLogic.Validations;
using SilentManager.API.Models.DB;
using SilentManager.API.Security;

namespace SilentManagerApiServer.BusinessLogic.Service
{
    public class EventService : IEventService
    {
        private readonly IUnitOfWork _unitOfWork;
        private readonly EventValidations eventValid;

        public EventService(IUnitOfWork unitOfWork)
        {
            this._unitOfWork = unitOfWork;
            this.eventValid = new EventValidations();
        }

        public async Task<ResultWrapper<CompletedEvent>> CreateEvent(CompletedEvent newEvent, TokenDto token)
        {
            if (!TokenValidation.ValidateToken(token, _unitOfWork))
            {
                List<ErrorResult> error = new List<ErrorResult>();
                error.Add(new ErrorResult(401, "Invalid token."));
                _unitOfWork.tokenRepo.Remove(_unitOfWork.tokenRepo.Find(tk => tk.token.Equals(token.AccessToken)).FirstOrDefault());
                return new ResultWrapper<CompletedEvent>(null, error);
            }

            var errors = eventValid.ValidateEventCreation(newEvent);

            if (errors.Count() > 0)
            {
                return new ResultWrapper<CompletedEvent>(null, errors);
            }

            CompletedEvent existEvent = null;
            if (newEvent.EventId.HasValue)
                existEvent = await GetEvent(newEvent.EventId.Value);

            if (existEvent != null)
            {
                return await UpdateEvent(existEvent, newEvent, token);
            }

            Event ev = Mapper.Map<Event>(newEvent);
            Location location = Mapper.Map<Location>(newEvent.Location);

            // Insert a new location in DB
            _unitOfWork.LocationRepo.AddAsync(location);
            await _unitOfWork.LocationRepo.SaveAsync();
            // Get user from token
            var userId = TokenValidation.GetUserIdFromToken(token, _unitOfWork);

            ev.Author = userId;
            ev.Location = location.LocationId;
            _unitOfWork.EventRepo.AddAsync(ev);
            await _unitOfWork.EventRepo.SaveAsync();

            Category c = _unitOfWork.categoriesRepo.Find(e => e.Category1.Equals(newEvent.Category)).FirstOrDefault();
            EventHasCategories ehc = new EventHasCategories();
            ehc.EventId = ev.EventId;
            ehc.CategoryId = c.CategoryId;
            _unitOfWork.eventHasCategories.Add(ehc);
            await _unitOfWork.eventHasCategories.SaveAsync();

            return new ResultWrapper<CompletedEvent>(newEvent, null);
        }

        private async Task<ResultWrapper<CompletedEvent>> UpdateEvent(CompletedEvent existingEvent, CompletedEvent newEvent, TokenDto token)
        {
            var tokenOwnerId = TokenValidation.GetUserIdFromToken(token, _unitOfWork);
            if (tokenOwnerId != existingEvent.Author.UserId)
            {
                List<ErrorResult> error = new List<ErrorResult>();
                error.Add(new ErrorResult(401, "Unauthorized operation."));
                return new ResultWrapper<CompletedEvent>(null, error);
            }

            // Update Location
            var location = _unitOfWork.LocationRepo.Find(l => l.LocationId == existingEvent.Location.LocationId).FirstOrDefault();
            location.Latitude = newEvent.Location.Latitude;
            location.Longitude = newEvent.Location.Longitude;
            location.Address = newEvent.Location.Address;

            await _unitOfWork.LocationRepo.SaveAsync();

            // Update Event
            var eventToUpdate = _unitOfWork.EventRepo.Find(x => x.EventId == existingEvent.EventId).FirstOrDefault();
            eventToUpdate.Description = newEvent.Description;
            eventToUpdate.StartDate = newEvent.StartDate;
            eventToUpdate.EndDate = newEvent.EndDate;
            eventToUpdate.Radius = newEvent.Radius;
            eventToUpdate.Name = newEvent.Name;

            await _unitOfWork.EventRepo.SaveAsync();

            EventHasCategories evCategories = _unitOfWork.eventHasCategories.Find(evt => evt.EventId == newEvent.EventId).FirstOrDefault();
            Category cf = _unitOfWork.categoriesRepo.Get(evCategories.CategoryId);
            if (!cf.Category1.Equals(newEvent.Category))
            {
                Category newCate = _unitOfWork.categoriesRepo.Find(ct => ct.Category1.Equals(newEvent.Category)).FirstOrDefault();
                _unitOfWork.eventHasCategories.Remove(evCategories);

                EventHasCategories newCategory = new EventHasCategories
                {
                    CategoryId = newCate.CategoryId,
                    EventId = evCategories.EventId
                };

                _unitOfWork.eventHasCategories.Add(newCategory);
                await _unitOfWork.eventHasCategories.SaveAsync();
            }

            return new ResultWrapper<CompletedEvent>(newEvent, null);
        }

        public async Task<ResultWrapper<PagedResult<CompletedEvent>>> GetUserEvents(TokenDto token, int? currentPage)
        {
            int currIndexPage = currentPage == null ? 0 : currentPage.Value;

            if (token == null || !TokenValidation.ValidateToken(token, _unitOfWork))
            {
                List<ErrorResult> errors = new List<ErrorResult>();
                errors.Add(new ErrorResult(401, "Invalid token."));
                return new ResultWrapper<PagedResult<CompletedEvent>>(null, errors);
            }

            int userId = TokenValidation.GetUserIdFromToken(token, _unitOfWork);
            var events = await _unitOfWork.EventRepo.GetEventsByUserAsync(userId);

            PagedResult<CompletedEvent> pg = new PagedResult<CompletedEvent>(currIndexPage, events.Count(), 25, Mapper.Map<IEnumerable<CompletedEvent>>(events));
            return new ResultWrapper<PagedResult<CompletedEvent>>(pg, null);
        }

        public IEnumerable<CompletedEvent> GetEvents(Expression<Func<Event, bool>> predicate)
        {
            var events = _unitOfWork.EventRepo.Find(predicate);
            return Mapper.Map<IEnumerable<CompletedEvent>>(events);
        }

        public async Task<CompletedEvent> GetEvent(int id)
        {
            var events = await _unitOfWork.EventRepo.GetAsync(id);

            if (events == null)
                return null;

            var user = await _unitOfWork.UserRepo.GetAsync(events.Author.Value);
            var location = await _unitOfWork.LocationRepo.GetAsync(events.Location.Value);

            return new CompletedEvent
            {
                Name = events.Name,
                EventId = events.EventId,
                Description = events.Description,
                Author = new CompletedUser
                {
                    Name = user.Name,
                    UserId = user.UserId,
                    Email = user.Email
                },
                EndDate = events.EndDate,
                StartDate = events.StartDate,
                Radius = events.Radius,
                Location = new CompletedLocation
                {
                    Address = location.Address,
                    Latitude = location.Latitude,
                    LocationId = location.LocationId,
                    Longitude = location.Longitude
                },
            };
        }

        public async Task<ResultWrapper<PagedResult<Event>>> GetEventsByUser(int userId, int currentPage)
        {
            var events = _unitOfWork.EventRepo.Find(ev => ev.Author == userId);

            PagedResult<Event> pg = new PagedResult<Event>(currentPage, events.Count(), 25, Mapper.Map<IEnumerable<Event>>(events));
            return new ResultWrapper<PagedResult<Event>>(pg, null);
        }

        public Task<CompletedEvent> UpdateEvent(CompletedEvent newEvent)
        {
            throw new NotImplementedException();
        }

        public async Task<ResultWrapper<PagedResult<CompletedEvent>>> GetEvents(double latitude, double longitude, int radius, int currentPage, string category, DateTime? date)
        {
            var errors = eventValid.ValidateLocation(latitude, longitude);
            if (errors.Count() > 0)
            {
                return new ResultWrapper<PagedResult<CompletedEvent>>(null, errors);
            }

            var pageSize = 25;

            if (category == "Any")
                category = "";

            var events = await _unitOfWork.EventRepo.GetEventsByDistanceRangeAsync(latitude, longitude, radius, category, date);

            var pagedEvents = events
                .Skip((currentPage - 1) * pageSize)
                .Take(pageSize);

            PagedResult<CompletedEvent> pg = new PagedResult<CompletedEvent>(currentPage, events.Count(), pageSize, Mapper.Map<IEnumerable<CompletedEvent>>(events));
            return new ResultWrapper<PagedResult<CompletedEvent>>(pg, null);
        }

        public async Task<bool> DeleteEvent(int eventId, TokenDto tk)
        {
            if (!TokenValidation.ValidateToken(tk, _unitOfWork))
            {
                List<ErrorResult> error = new List<ErrorResult>();
                error.Add(new ErrorResult(401, "Invalid token."));
                _unitOfWork.tokenRepo.Remove(_unitOfWork.tokenRepo.Find(token => token.token.Equals(tk.AccessToken)).FirstOrDefault());
                return false;
            }

            Event deleteEvent = await _unitOfWork.EventRepo.GetAsync(eventId);

            var tokenAuthor = TokenValidation.GetUserIdFromToken(tk, _unitOfWork);
            if (deleteEvent.Author != tokenAuthor)
            {
                List<ErrorResult> error = new List<ErrorResult>();
                return false;
            }

            // Remove
            EventHasCategories evCategories = _unitOfWork.eventHasCategories.Find(evt => evt.EventId == deleteEvent.EventId).FirstOrDefault();
            _unitOfWork.eventHasCategories.Remove(evCategories);
            await _unitOfWork.eventHasCategories.SaveAsync();

            _unitOfWork.InviteRepo.RemoveRange(_unitOfWork.InviteRepo.Find((iv) => iv.EventId == deleteEvent.EventId));
            await _unitOfWork.InviteRepo.SaveAsync();

            _unitOfWork.LocationRepo.Remove(_unitOfWork.LocationRepo.Get(deleteEvent.Location.Value));
            await _unitOfWork.LocationRepo.SaveAsync();

            _unitOfWork.EventRepo.Remove(deleteEvent);
            await _unitOfWork.EventRepo.SaveAsync();

            return true;
        }
    }
}
